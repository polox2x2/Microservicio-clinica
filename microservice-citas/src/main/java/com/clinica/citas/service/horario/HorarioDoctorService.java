package com.clinica.citas.service.horario;

import com.clinica.citas.dto.horario.CupoDisponibleResponseDto;
import com.clinica.citas.dto.horario.HorarioDoctorRequestDto;
import com.clinica.citas.dto.horario.HorarioDoctorResponseDto;
import com.clinica.citas.exception.BusinessRuleException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.mapper.horario.HorarioDoctorMapper;
import com.clinica.citas.model.cita.Cita;
import com.clinica.citas.model.cita.EstadoCita;
import com.clinica.citas.model.horario.HorarioDoctor;
import com.clinica.citas.model.usuario.RolUsuario;
import com.clinica.citas.model.usuario.Usuario;
import com.clinica.citas.repository.cita.CitaRepository;
import com.clinica.citas.repository.horario.HorarioDoctorRepository;
import com.clinica.citas.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

//gestiona bloques horarios y calcula cupos libres reales
@Service
@RequiredArgsConstructor
public class HorarioDoctorService {

    private final HorarioDoctorRepository horarioDoctorRepository;
    private final CitaRepository citaRepository;
    private final UsuarioService usuarioService;
    private final HorarioDoctorMapper horarioDoctorMapper;

    @Transactional(readOnly = true)
    public List<HorarioDoctorResponseDto> listarTodos() {
        return horarioDoctorRepository.findAll().stream()
                .map(horarioDoctorMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HorarioDoctorResponseDto> listarPorDoctor(Long doctorId, boolean soloActivos) {
        List<HorarioDoctor> horarios = soloActivos
                ? horarioDoctorRepository.findByDoctorIdAndActivoTrue(doctorId)
                : horarioDoctorRepository.findByDoctorId(doctorId);
        return horarios.stream()
                .map(horarioDoctorMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public HorarioDoctorResponseDto obtenerPorId(Long id) {
        return horarioDoctorMapper.toDto(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<CupoDisponibleResponseDto> consultarDisponibilidad(Long doctorId, LocalDate fecha) {
        Usuario doctor = validarDoctorActivo(doctorId);
        List<HorarioDoctor> bloques = horarioDoctorRepository
                .findByDoctorIdAndDiaSemanaAndActivoTrueOrderByHoraInicioAsc(doctor.getId(), fecha.getDayOfWeek());
        if (bloques.isEmpty()) {
            return List.of();
        }

        List<Cita> citasOcupadas = citaRepository.findByDoctorIdAndFechaAndEstadoNotOrderByHoraInicioAsc(
                doctor.getId(), fecha, EstadoCita.CANCELADA);

        //genera intervalos libres cruzando bloques activos contra citas vigentes
        List<CupoDisponibleResponseDto> cuposDisponibles = new ArrayList<>();
        String nombreDoctor = doctor.getNombres() + " " + doctor.getApellidos();

        for (HorarioDoctor bloque : bloques) {
            int duracion = bloque.getDuracionMinutos() != null ? bloque.getDuracionMinutos() : 30;
            LocalTime cursor = bloque.getHoraInicio();
            while (true) {
                LocalTime finCupo = cursor.plusMinutes(duracion);
                if (!finCupo.isAfter(cursor) || finCupo.isAfter(bloque.getHoraFin())) {
                    break;
                }
                LocalTime inicioActual = cursor;
                boolean ocupado = citasOcupadas.stream()
                        .anyMatch(c -> c.getHoraInicio().isBefore(finCupo) && c.getHoraFin().isAfter(inicioActual));
                if (!ocupado) {
                    cuposDisponibles.add(CupoDisponibleResponseDto.builder()
                            .horarioId(bloque.getId())
                            .doctorId(doctor.getId())
                            .doctorNombre(nombreDoctor)
                            .fecha(fecha)
                            .horaInicio(inicioActual)
                            .horaFin(finCupo)
                            .consultorio(bloque.getConsultorio())
                            .duracionMinutos(duracion)
                            .build());
                }
                cursor = finCupo;
            }
        }

        return cuposDisponibles;
    }

    @Transactional
    public HorarioDoctorResponseDto crear(HorarioDoctorRequestDto request) {
        validarRangoHorario(request.getHoraInicio(), request.getHoraFin(), request.getDuracionMinutos());
        Usuario doctor = validarDoctorActivo(request.getDoctorId());
        boolean activo = request.getActivo() == null || request.getActivo();

        if (activo) {
            validarSinSolapamiento(doctor.getId(), request.getDiaSemana(),
                    request.getHoraInicio(), request.getHoraFin(), null);
        }

        HorarioDoctor horario = horarioDoctorMapper.toEntity(request);
        horario.setDoctor(doctor);
        horario.setConsultorio(request.getConsultorio().trim());
        horario.setActivo(activo);

        return horarioDoctorMapper.toDto(horarioDoctorRepository.save(horario));
    }

    @Transactional
    public HorarioDoctorResponseDto actualizar(Long id, HorarioDoctorRequestDto request) {
        HorarioDoctor horario = obtenerEntidad(id);
        validarRangoHorario(request.getHoraInicio(), request.getHoraFin(), request.getDuracionMinutos());
        Usuario doctor = validarDoctorActivo(request.getDoctorId());
        boolean activoResultante = request.getActivo() != null
                ? request.getActivo()
                : Boolean.TRUE.equals(horario.getActivo());

        if (activoResultante) {
            validarSinSolapamiento(doctor.getId(), request.getDiaSemana(),
                    request.getHoraInicio(), request.getHoraFin(), id);
        }

        horarioDoctorMapper.updateEntity(request, horario);
        horario.setDoctor(doctor);
        horario.setConsultorio(request.getConsultorio().trim());
        horario.setActivo(activoResultante);

        return horarioDoctorMapper.toDto(horarioDoctorRepository.save(horario));
    }

    @Transactional
    public HorarioDoctorResponseDto cambiarEstado(Long id, boolean activo) {
        HorarioDoctor horario = obtenerEntidad(id);
        if (activo) {
            validarDoctorActivo(horario.getDoctor().getId());
            validarSinSolapamiento(horario.getDoctor().getId(), horario.getDiaSemana(),
                    horario.getHoraInicio(), horario.getHoraFin(), id);
        }
        horario.setActivo(activo);
        return horarioDoctorMapper.toDto(horarioDoctorRepository.save(horario));
    }

    @Transactional
    public void eliminar(Long id) {
        HorarioDoctor horario = obtenerEntidad(id);
        horarioDoctorRepository.delete(horario);
    }

    private HorarioDoctor obtenerEntidad(Long id) {
        return horarioDoctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario médico no encontrado con id: " + id));
    }

    private Usuario validarDoctorActivo(Long doctorId) {
        Usuario doctor = usuarioService.obtenerEntidad(doctorId);
        if (doctor.getRol() != RolUsuario.DOCTOR) {
            throw new BusinessRuleException("El usuario con id " + doctorId + " no tiene el rol DOCTOR");
        }
        if (!Boolean.TRUE.equals(doctor.getActivo())) {
            throw new BusinessRuleException("El doctor seleccionado no se encuentra activo");
        }
        return doctor;
    }

    private void validarRangoHorario(LocalTime horaInicio, LocalTime horaFin, Integer duracionMinutos) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new BusinessRuleException("La horaFin debe ser estrictamente posterior a la horaInicio");
        }
        long minutosBloque = Duration.between(horaInicio, horaFin).toMinutes();
        if (duracionMinutos == null || minutosBloque < duracionMinutos) {
            throw new BusinessRuleException(
                    "El rango horario debe ser al menos igual a la duración en minutos configurada");
        }
    }

    private void validarSinSolapamiento(
            Long doctorId,
            DayOfWeek diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long excludeId) {
        if (horarioDoctorRepository.existeSolapamiento(doctorId, diaSemana, horaInicio, horaFin, excludeId)) {
            throw new BusinessRuleException(
                    "El bloque horario se solapa con otro horario activo del doctor para el día " + diaSemana);
        }
    }
}
