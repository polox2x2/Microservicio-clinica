package com.clinica.citas.service.horario;

import com.clinica.citas.dto.horario.HorarioDoctorRequestDto;
import com.clinica.citas.dto.horario.HorarioDoctorResponseDto;
import com.clinica.citas.exception.BusinessRuleException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.mapper.horario.HorarioDoctorMapper;
import com.clinica.citas.model.horario.HorarioDoctor;
import com.clinica.citas.model.usuario.RolUsuario;
import com.clinica.citas.model.usuario.Usuario;
import com.clinica.citas.repository.horario.HorarioDoctorRepository;
import com.clinica.citas.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

// Lógica de negocio de horarios
@Service
@RequiredArgsConstructor
public class HorarioDoctorService {

    private final HorarioDoctorRepository horarioDoctorRepository;
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

    @Transactional
    public HorarioDoctorResponseDto crear(HorarioDoctorRequestDto request) {
        validarRangoHorario(request.getHoraInicio(), request.getHoraFin());
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
        validarRangoHorario(request.getHoraInicio(), request.getHoraFin());
        Usuario doctor = validarDoctorActivo(request.getDoctorId());
        boolean activoResultante = request.getActivo() != null ? request.getActivo() : Boolean.TRUE.equals(horario.getActivo());

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

    private void validarRangoHorario(LocalTime horaInicio, LocalTime horaFin) {
        if (!horaFin.isAfter(horaInicio)) {
            throw new BusinessRuleException("La horaFin debe ser estrictamente posterior a la horaInicio");
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
