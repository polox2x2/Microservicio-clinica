package com.clinica.citas.dto.horario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

// Datos de entrada para horarios médicos
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDoctorRequestDto {

    @NotNull(message = "El doctorId es obligatorio")
    private Long doctorId;

    @NotNull(message = "El día de la semana es obligatorio")
    private DayOfWeek diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotBlank(message = "El consultorio es obligatorio")
    @Size(max = 50, message = "El consultorio no debe exceder 50 caracteres")
    private String consultorio;

    private Boolean activo;
}
