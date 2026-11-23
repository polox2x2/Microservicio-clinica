package com.clinica.citas.repository.cita;

import com.clinica.citas.model.cita.Cita;
import com.clinica.citas.model.cita.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findAllByOrderByFechaDescHoraInicioDesc();

    List<Cita> findByPacienteIdOrderByFechaDescHoraInicioDesc(Long pacienteId);

    List<Cita> findByPacienteIdAndEstadoOrderByFechaDescHoraInicioDesc(Long pacienteId, EstadoCita estado);

    List<Cita> findByDoctorIdOrderByFechaDescHoraInicioDesc(Long doctorId);

    List<Cita> findByDoctorIdAndFechaOrderByHoraInicioAsc(Long doctorId, LocalDate fecha);

    List<Cita> findByDoctorIdAndFechaAndEstadoNotOrderByHoraInicioAsc(
            Long doctorId,
            LocalDate fecha,
            EstadoCita estadoExcluido
    );

    List<Cita> findByFechaOrderByHoraInicioAsc(LocalDate fecha);

    @Query("""
            SELECT c FROM Cita c
            WHERE (:pacienteId IS NULL OR c.paciente.id = :pacienteId)
              AND (:doctorId IS NULL OR c.doctor.id = :doctorId)
              AND (:estado IS NULL OR c.estado = :estado)
              AND (:fechaInicio IS NULL OR c.fecha >= :fechaInicio)
              AND (:fechaFin IS NULL OR c.fecha <= :fechaFin)
            ORDER BY c.fecha DESC, c.horaInicio DESC
            """)
    List<Cita> buscarHistorial(
            @Param("pacienteId") Long pacienteId,
            @Param("doctorId") Long doctorId,
            @Param("estado") EstadoCita estado,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("""
            SELECT COUNT(c) > 0 FROM Cita c
            WHERE c.doctor.id = :doctorId
              AND c.fecha = :fecha
              AND c.estado <> :estadoCancelada
              AND (:excludeId IS NULL OR c.id <> :excludeId)
              AND c.horaInicio < :horaFin
              AND c.horaFin > :horaInicio
            """)
    boolean existeChoqueDoctor(
            @Param("doctorId") Long doctorId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estadoCancelada") EstadoCita estadoCancelada,
            @Param("excludeId") Long excludeId
    );

    @Query("""
            SELECT COUNT(c) > 0 FROM Cita c
            WHERE c.paciente.id = :pacienteId
              AND c.fecha = :fecha
              AND c.estado <> :estadoCancelada
              AND (:excludeId IS NULL OR c.id <> :excludeId)
              AND c.horaInicio < :horaFin
              AND c.horaFin > :horaInicio
            """)
    boolean existeChoquePaciente(
            @Param("pacienteId") Long pacienteId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("estadoCancelada") EstadoCita estadoCancelada,
            @Param("excludeId") Long excludeId
    );
}
