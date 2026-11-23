package com.clinica.citas.dto.cita;

import com.clinica.citas.model.cita.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaResponseDto {

    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private Long doctorId;
    private String doctorNombre;
    private Long especialidadId;
    private String especialidadNombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String motivo;
    private String observaciones;
    private String notasClinicas;
    private EstadoCita estado;
    private LocalDateTime fechaCreacion;
}
