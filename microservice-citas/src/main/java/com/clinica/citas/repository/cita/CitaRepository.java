package com.clinica.citas.repository.cita;

import com.clinica.citas.model.cita.Cita;
import com.clinica.citas.model.cita.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Repositorio JPA de citas médicas
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPacienteId(Long pacienteId);

    List<Cita> findByDoctorId(Long doctorId);

    List<Cita> findByFecha(LocalDate fecha);

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
