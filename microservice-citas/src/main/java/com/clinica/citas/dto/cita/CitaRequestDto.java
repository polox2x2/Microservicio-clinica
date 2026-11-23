package com.clinica.citas.dto.cita;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CitaRequestDto {

    @NotNull(message = "El pacienteId es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El doctorId es obligatorio")
    private Long doctorId;

    @NotNull(message = "La especialidadId es obligatoria")
    private Long especialidadId;

    @NotNull(message = "La fecha de la cita es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotBlank(message = "El motivo de la cita es obligatorio")
    @Size(max = 300, message = "El motivo no debe exceder 300 caracteres")
    private String motivo;

    @Size(max = 500, message = "Las observaciones no deben exceder 500 caracteres")
    private String observaciones;
}
