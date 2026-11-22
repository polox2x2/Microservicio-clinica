package com.clinica.citas.repository.horario;

import com.clinica.citas.model.horario.HorarioDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

// Repositorio JPA de horarios médicos
public interface HorarioDoctorRepository extends JpaRepository<HorarioDoctor, Long> {

    List<HorarioDoctor> findByDoctorId(Long doctorId);

    List<HorarioDoctor> findByDoctorIdAndActivoTrue(Long doctorId);

    @Query("""
            SELECT COUNT(h) > 0 FROM HorarioDoctor h
            WHERE h.doctor.id = :doctorId
              AND h.diaSemana = :diaSemana
              AND h.activo = true
              AND (:excludeId IS NULL OR h.id <> :excludeId)
              AND h.horaInicio < :horaFin
              AND h.horaFin > :horaInicio
            """)
    boolean existeSolapamiento(
            @Param("doctorId") Long doctorId,
            @Param("diaSemana") DayOfWeek diaSemana,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("excludeId") Long excludeId
    );

    @Query("""
            SELECT COUNT(h) > 0 FROM HorarioDoctor h
            WHERE h.doctor.id = :doctorId
              AND h.diaSemana = :diaSemana
              AND h.activo = true
              AND h.horaInicio <= :horaInicio
              AND h.horaFin >= :horaFin
            """)
    boolean existeHorarioDisponible(
            @Param("doctorId") Long doctorId,
            @Param("diaSemana") DayOfWeek diaSemana,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );
}
