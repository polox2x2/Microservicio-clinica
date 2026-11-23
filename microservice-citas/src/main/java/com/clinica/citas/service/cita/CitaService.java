package com.clinica.citas.service.cita;

import com.clinica.citas.client.NotificacionClient;
import com.clinica.citas.client.NotificacionClientRequestDto;
import com.clinica.citas.dto.cita.AtenderCitaRequestDto;
import com.clinica.citas.dto.cita.CitaRequestDto;
import com.clinica.citas.dto.cita.CitaResponseDto;
import com.clinica.citas.dto.cita.ReprogramarCitaRequestDto;
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

//gestiona ciclo de vida de citas medicas y notificaciones feign
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
        return citaRepository.findAllByOrderByFechaDescHoraInicioDesc().stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CitaResponseDto obtenerPorId(Long id) {
        return citaMapper.toDto(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDto> listarPorPaciente(Long pacienteId, EstadoCita estado) {
        List<Cita> citas = estado != null
                ? citaRepository.findByPacienteIdAndEstadoOrderByFechaDescHoraInicioDesc(pacienteId, estado)
                : citaRepository.findByPacienteIdOrderByFechaDescHoraInicioDesc(pacienteId);
        return citas.stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDto> listarPorDoctor(Long doctorId, LocalDate fecha) {
        List<Cita> citas = fecha != null
                ? citaRepository.findByDoctorIdAndFechaOrderByHoraInicioAsc(doctorId, fecha)
                : citaRepository.findByDoctorIdOrderByFechaDescHoraInicioDesc(doctorId);
        return citas.stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDto> listarPorFecha(LocalDate fecha) {
        return citaRepository.findByFechaOrderByHoraInicioAsc(fecha).stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDto> buscarHistorial(
            Long pacienteId,
            Long doctorId,
            EstadoCita estado,
            LocalDate fechaInicio,
            LocalDate fechaFin) {
        return citaRepository.buscarHistorial(pacienteId, doctorId, estado, fechaInicio, fechaFin).stream()
                .map(citaMapper::toDto)
                .toList();
    }

    @Transactional
    public CitaResponseDto reservar(CitaRequestDto request) {
        CitaDatosValidados datos = validarReglasCita(request);

        Cita cita = citaMapper.toEntity(request);
        cita.setPaciente(datos.paciente());
        cita.setDoctor(datos.doctor());
        cita.setEspecialidad(datos.especialidad());
        cita.setMotivo(request.getMotivo().trim());
        if (request.getObservaciones() != null && !request.getObservaciones().isBlank()) {
            cita.setObservaciones(request.getObservaciones().trim());
        }
        cita.setEstado(EstadoCita.RESERVADA);

        Cita guardada = citaRepository.save(cita);
        notificarEventoCita(guardada, "CONFIRMACION_CITA", "Reserva de Cita Médica",
                construirMensajeCita(guardada, "ha sido reservada exitosamente"));
        return citaMapper.toDto(guardada);
    }

    @Transactional
    public CitaResponseDto reprogramar(Long id, ReprogramarCitaRequestDto request) {
        Cita cita = obtenerEntidad(id);
        validarTransicionPendiente(cita, "reprogramar");
        validarRangoHoras(request.getHoraInicio(), request.getHoraFin());

        Usuario paciente = validarPacienteActivo(cita.getPaciente().getId());
        Usuario doctor = validarDoctorActivo(cita.getDoctor().getId());
        validarDoctorEnEspecialidad(doctor, cita.getEspecialidad());
        validarCoberturaHorario(doctor.getId(), request.getFecha(), request.getHoraInicio(), request.getHoraFin());
        validarDisponibilidadSinChoques(
                doctor.getId(),
                paciente.getId(),
                request.getFecha(),
                request.getHoraInicio(),
                request.getHoraFin(),
                id
        );

        cita.setFecha(request.getFecha());
        cita.setHoraInicio(request.getHoraInicio());
        cita.setHoraFin(request.getHoraFin());
        if (request.getMotivo() != null && !request.getMotivo().isBlank()) {
            cita.setMotivo(request.getMotivo().trim());
        }
        if (request.getObservaciones() != null && !request.getObservaciones().isBlank()) {
            cita.setObservaciones(request.getObservaciones().trim());
        }
        cita.setEstado(EstadoCita.RESERVADA);

        Cita guardada = citaRepository.save(cita);
        notificarEventoCita(guardada, "REPROGRAMACION_CITA", "Reprogramación de Cita Médica",
                construirMensajeCita(guardada, "ha sido reprogramada"));
        return citaMapper.toDto(guardada);
    }

    @Transactional
    public CitaResponseDto confirmar(Long id) {
        Cita cita = obtenerEntidad(id);
        if (cita.getEstado() != EstadoCita.RESERVADA) {
            throw new BusinessRuleException("Solo se pueden confirmar citas en estado RESERVADA");
        }
        cita.setEstado(EstadoCita.CONFIRMADA);
        Cita guardada = citaRepository.save(cita);
        notificarEventoCita(guardada, "CONFIRMACION_CITA", "Cita Médica Confirmada",
                construirMensajeCita(guardada, "ha sido confirmada"));
        return citaMapper.toDto(guardada);
    }

    @Transactional
    public CitaResponseDto atender(Long id, AtenderCitaRequestDto request) {
        Cita cita = obtenerEntidad(id);
        validarTransicionPendiente(cita, "atender");
        cita.setNotasClinicas(request.getNotasClinicas().trim());
        if (request.getObservaciones() != null && !request.getObservaciones().isBlank()) {
            cita.setObservaciones(request.getObservaciones().trim());
        }
        cita.setEstado(EstadoCita.ATENDIDA);
        return citaMapper.toDto(citaRepository.save(cita));
    }

    @Transactional
    public CitaResponseDto cancelar(Long id, String motivoCancelacion) {
        Cita cita = obtenerEntidad(id);
        validarTransicionPendiente(cita, "cancelar");
        if (motivoCancelacion != null && !motivoCancelacion.isBlank()) {
            String obsActual = cita.getObservaciones();
            String motivoLimpio = motivoCancelacion.trim();
            String nuevaObs = (obsActual != null && !obsActual.isBlank())
                    ? obsActual + " | Cancelación: " + motivoLimpio
                    : "Cancelación: " + motivoLimpio;
            cita.setObservaciones(nuevaObs.length() > 500 ? nuevaObs.substring(0, 500) : nuevaObs);
        }
        cita.setEstado(EstadoCita.CANCELADA);
        Cita guardada = citaRepository.save(cita);
        notificarEventoCita(guardada, "CANCELACION_CITA", "Cita Médica Cancelada",
                construirMensajeCita(guardada, "ha sido cancelada"));
        return citaMapper.toDto(guardada);
    }

    @Transactional
    public CitaResponseDto marcarNoAsistio(Long id) {
        Cita cita = obtenerEntidad(id);
        validarTransicionPendiente(cita, "marcar como NO_ASISTIO");
        cita.setEstado(EstadoCita.NO_ASISTIO);
        return citaMapper.toDto(citaRepository.save(cita));
    }

    //envia notificacion tolerante a fallos para no revertir la transaccion de la cita
    private void notificarEventoCita(Cita cita, String tipoEvento, String asunto, String mensaje) {
        try {
            Usuario paciente = cita.getPaciente();
            boolean tieneTelefono = paciente.getTelefono() != null && !paciente.getTelefono().isBlank();
            String canal = tieneTelefono ? "WHATSAPP" : "EMAIL";
            String contacto = tieneTelefono ? paciente.getTelefono() : paciente.getEmail();

            NotificacionClientRequestDto payload = NotificacionClientRequestDto.builder()
                    .destinatarioId(paciente.getId())
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

    private CitaDatosValidados validarReglasCita(CitaRequestDto request) {
        validarRangoHoras(request.getHoraInicio(), request.getHoraFin());
        Usuario paciente = validarPacienteActivo(request.getPacienteId());
        Usuario doctor = validarDoctorActivo(request.getDoctorId());

        Especialidad especialidad = especialidadService.obtenerEntidad(request.getEspecialidadId());
        if (!Boolean.TRUE.equals(especialidad.getActivo())) {
            throw new BusinessRuleException("La especialidad seleccionada no se encuentra activa");
        }

        validarDoctorEnEspecialidad(doctor, especialidad);
        validarCoberturaHorario(doctor.getId(), request.getFecha(), request.getHoraInicio(), request.getHoraFin());
        validarDisponibilidadSinChoques(
                doctor.getId(),
                paciente.getId(),
                request.getFecha(),
                request.getHoraInicio(),
                request.getHoraFin(),
                null
        );

        return new CitaDatosValidados(paciente, doctor, especialidad);
    }

    private void validarRangoHoras(LocalTime horaInicio, LocalTime horaFin) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new BusinessRuleException("La horaFin de la cita debe ser posterior a la horaInicio");
        }
    }

    private Usuario validarPacienteActivo(Long pacienteId) {
        Usuario paciente = usuarioService.obtenerEntidad(pacienteId);
        if (paciente.getRol() != RolUsuario.PACIENTE || !Boolean.TRUE.equals(paciente.getActivo())) {
            throw new BusinessRuleException("El usuario paciente debe tener rol PACIENTE y estar activo");
        }
        return paciente;
    }

    private Usuario validarDoctorActivo(Long doctorId) {
        Usuario doctor = usuarioService.obtenerEntidad(doctorId);
        if (doctor.getRol() != RolUsuario.DOCTOR || !Boolean.TRUE.equals(doctor.getActivo())) {
            throw new BusinessRuleException("El usuario doctor debe tener rol DOCTOR y estar activo");
        }
        return doctor;
    }

    private void validarDoctorEnEspecialidad(Usuario doctor, Especialidad especialidad) {
        if (doctor.getEspecialidad() == null || !doctor.getEspecialidad().getId().equals(especialidad.getId())) {
            throw new BusinessRuleException("El doctor seleccionado no pertenece a la especialidad indicada");
        }
    }

    private void validarCoberturaHorario(Long doctorId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        boolean cubiertoPorHorario = horarioDoctorRepository.existeHorarioDisponible(
                doctorId,
                fecha.getDayOfWeek(),
                horaInicio,
                horaFin
        );
        if (!cubiertoPorHorario) {
            throw new BusinessRuleException(
                    "El doctor no cuenta con un horario activo que cubra el rango solicitado en "
                            + fecha.getDayOfWeek());
        }
    }

    private void validarTransicionPendiente(Cita cita, String operacion) {
        if (cita.getEstado() != EstadoCita.RESERVADA && cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new BusinessRuleException(
                    "No se puede " + operacion + " una cita en estado " + cita.getEstado());
        }
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
