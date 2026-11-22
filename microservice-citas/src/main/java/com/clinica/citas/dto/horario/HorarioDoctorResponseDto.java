package com.clinica.citas.dto.horario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

// Datos de salida de un horario
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDoctorResponseDto {

    private Long id;
    private Long doctorId;
    private String doctorNombre;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String consultorio;
    private Boolean activo;
}
