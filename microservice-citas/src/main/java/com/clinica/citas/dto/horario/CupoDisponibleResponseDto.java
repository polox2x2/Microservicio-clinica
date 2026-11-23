package com.clinica.citas.dto.horario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CupoDisponibleResponseDto {

    private Long horarioId;
    private Long doctorId;
    private String doctorNombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String consultorio;
    private Integer duracionMinutos;
}
