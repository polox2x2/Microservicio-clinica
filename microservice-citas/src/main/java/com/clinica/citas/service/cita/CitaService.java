package com.clinica.citas.service.cita;

import com.clinica.citas.client.NotificacionClient;
import com.clinica.citas.client.NotificacionClientRequestDto;
import com.clinica.citas.dto.cita.CitaRequestDto;
import com.clinica.citas.dto.cita.CitaResponseDto;
import com.clinica.citas.exception.BusinessRuleException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.mapper.cita.CitaMapper;
import com.clinica.citas.model.cita.Cita;
import com.clinica.citas.model.cita.EstadoCita;
import com.clinica.citas.model.especialidad.Especialidad;
import com.clinica.citas.model.usuario.RolUsuario;
import com.clinica.citas.model.usuario.Usuario;
import com.clinica.citas.repository.cita.CitaRepository;
import com.clinica.citas.repository.horario.HorarioDoctorRepository;
import com.clinica.citas.service.especialidad.EspecialidadService;
import com.clinica.citas.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Lógica de negocio de citas
@Slf4j
@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final HorarioDoctorRepository horarioDoctorRepository;
    private final UsuarioService usuarioService;
    private final EspecialidadService especialidadService;
    private final NotificacionClient notificacionClient;
    private final CitaMapper citaMapper;

    @Transactional(readOnly = true)
    public List<CitaResponseDto> listarTodas() {
        return citaRepository.findAll().stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CitaResponseDto obtenerPorId(Long id) {
        return citaMapper.toDto(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDto> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId).stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDto> listarPorDoctor(Long doctorId) {
        return citaRepository.findByDoctorId(doctorId).stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDto> listarPorFecha(LocalDate fecha) {
        return citaRepository.findByFecha(fecha).stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional
    public CitaResponseDto reservar(CitaRequestDto request) {
        EstadoCita estado = request.getEstado() != null ? request.getEstado() : EstadoCita.PENDIENTE;
        CitaDatosValidados datos = validarReglasCita(request, estado, null);

        Cita cita = citaMapper.toEntity(request);
        cita.setPaciente(datos.paciente());
        cita.setDoctor(datos.doctor());
        cita.setEspecialidad(datos.especialidad());
        cita.setMotivo(request.getMotivo().trim());
        cita.setEstado(estado);

        Cita guardada = citaRepository.save(cita);
        if (guardada.getEstado() == EstadoCita.CANCELADA) {
            notificarEventoCita(guardada, "CANCELACION_CITA", "Cita Médica Cancelada",
                    construirMensajeCita(guardada, "ha sido registrada como cancelada"));
        } else {
            notificarEventoCita(guardada, "CONFIRMACION_CITA", "Reserva de Cita Médica",
                    construirMensajeCita(guardada, "ha sido reservada exitosamente"));
        }
        return citaMapper.toDto(guardada);
    }

    @Transactional
    public CitaResponseDto actualizar(Long id, CitaRequestDto request) {
        Cita cita = obtenerEntidad(id);
        EstadoCita estado = request.getEstado() != null ? request.getEstado() : cita.getEstado();
        CitaDatosValidados datos = validarReglasCita(request, estado, id);

        citaMapper.updateEntity(request, cita);
        cita.setPaciente(datos.paciente());
        cita.setDoctor(datos.doctor());
        cita.setEspecialidad(datos.especialidad());
        cita.setMotivo(request.getMotivo().trim());
        cita.setEstado(estado);

        return citaMapper.toDto(citaRepository.save(cita));
    }

    @Transactional
    public CitaResponseDto confirmar(Long id) {
        return cambiarEstado(id, EstadoCita.CONFIRMADA);
    }

    @Transactional
    public CitaResponseDto cambiarEstado(Long id, EstadoCita nuevoEstado) {
        Cita cita = obtenerEntidad(id);
        if (cita.getEstado() == EstadoCita.CANCELADA && nuevoEstado != EstadoCita.CANCELADA) {
            validarDisponibilidadSinChoques(
                    cita.getDoctor().getId(),
                    cita.getPaciente().getId(),
                    cita.getFecha(),
                    cita.getHoraInicio(),
                    cita.getHoraFin(),
                    id
            );
        }
        cita.setEstado(nuevoEstado);
        Cita guardada = citaRepository.save(cita);

        if (nuevoEstado == EstadoCita.CONFIRMADA) {
            notificarEventoCita(guardada, "CONFIRMACION_CITA", "Cita Médica Confirmada",
                    construirMensajeCita(guardada, "ha sido confirmada"));
        } else if (nuevoEstado == EstadoCita.CANCELADA) {
            notificarEventoCita(guardada, "CANCELACION_CITA", "Cita Médica Cancelada",
                    construirMensajeCita(guardada, "ha sido cancelada"));
        }

        return citaMapper.toDto(guardada);
    }

    private void notificarEventoCita(Cita cita, String tipoEvento, String asunto, String mensaje) {
        try {
            Usuario paciente = cita.getPaciente();
            boolean tieneTelefono = paciente.getTelefono() != null && !paciente.getTelefono().isBlank();
            String canal = tieneTelefono ? "WHATSAPP" : "EMAIL";
            String contacto = tieneTelefono ? paciente.getTelefono() : paciente.getEmail();

            NotificacionClientRequestDto payload = NotificacionClientRequestDto.builder()
                    .usuarioId(paciente.getId())
                    .destinatarioNombre(paciente.getNombres() + " " + paciente.getApellidos())
                    .destinatarioContacto(contacto)
                    .canal(canal)
                    .tipoEvento(tipoEvento)
                    .referenciaId(cita.getId())
                    .asunto(asunto)
                    .mensaje(mensaje)
                    .enviarInmediatamente(Boolean.TRUE)
                    .build();

            notificacionClient.enviarNotificacion(payload);
        } catch (Exception ex) {
            log.warn("No se pudo enviar notificación ({}) para la cita {}: {}",
                    tipoEvento, cita.getId(), ex.getMessage());
        }
    }

    private String construirMensajeCita(Cita cita, String accion) {
        return String.format("Estimado(a) %s, su cita de %s con el Dr(a). %s %s para el %s de %s a %s %s.",
                cita.getPaciente().getNombres(),
                cita.getEspecialidad().getNombre(),
                cita.getDoctor().getNombres(),
                cita.getDoctor().getApellidos(),
                cita.getFecha(),
                cita.getHoraInicio(),
                cita.getHoraFin(),
                accion);
    }

    private Cita obtenerEntidad(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
    }

    private CitaDatosValidados validarReglasCita(CitaRequestDto request, EstadoCita estado, Long excludeId) {
        if (!request.getHoraFin().isAfter(request.getHoraInicio())) {
            throw new BusinessRuleException("La horaFin de la cita debe ser posterior a la horaInicio");
        }

        Usuario paciente = usuarioService.obtenerEntidad(request.getPacienteId());
        if (paciente.getRol() != RolUsuario.PACIENTE || !Boolean.TRUE.equals(paciente.getActivo())) {
            throw new BusinessRuleException("El usuario paciente debe tener rol PACIENTE y estar activo");
        }

        Usuario doctor = usuarioService.obtenerEntidad(request.getDoctorId());
        if (doctor.getRol() != RolUsuario.DOCTOR || !Boolean.TRUE.equals(doctor.getActivo())) {
            throw new BusinessRuleException("El usuario doctor debe tener rol DOCTOR y estar activo");
        }

        Especialidad especialidad = especialidadService.obtenerEntidad(request.getEspecialidadId());
        if (!Boolean.TRUE.equals(especialidad.getActivo())) {
            throw new BusinessRuleException("La especialidad seleccionada no se encuentra activa");
        }

        if (doctor.getEspecialidad() == null || !doctor.getEspecialidad().getId().equals(especialidad.getId())) {
            throw new BusinessRuleException("El doctor seleccionado no pertenece a la especialidad indicada");
        }

        if (estado != EstadoCita.CANCELADA) {
            boolean cubiertoPorHorario = horarioDoctorRepository.existeHorarioDisponible(
                    doctor.getId(),
                    request.getFecha().getDayOfWeek(),
                    request.getHoraInicio(),
                    request.getHoraFin()
            );
            if (!cubiertoPorHorario) {
                throw new BusinessRuleException(
                        "El doctor no cuenta con un horario activo que cubra el rango solicitado en "
                                + request.getFecha().getDayOfWeek());
            }

            validarDisponibilidadSinChoques(
                    doctor.getId(),
                    paciente.getId(),
                    request.getFecha(),
                    request.getHoraInicio(),
                    request.getHoraFin(),
                    excludeId
            );
        }

        return new CitaDatosValidados(paciente, doctor, especialidad);
    }

    private void validarDisponibilidadSinChoques(
            Long doctorId,
            Long pacienteId,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long excludeId) {
        if (citaRepository.existeChoqueDoctor(
                doctorId, fecha, horaInicio, horaFin, EstadoCita.CANCELADA, excludeId)) {
            throw new BusinessRuleException("El doctor ya tiene una cita activa que se cruza con ese horario");
        }
        if (citaRepository.existeChoquePaciente(
                pacienteId, fecha, horaInicio, horaFin, EstadoCita.CANCELADA, excludeId)) {
            throw new BusinessRuleException("El paciente ya tiene otra cita activa que se cruza con ese horario");
        }
    }

    private record CitaDatosValidados(Usuario paciente, Usuario doctor, Especialidad especialidad) {
    }
}
