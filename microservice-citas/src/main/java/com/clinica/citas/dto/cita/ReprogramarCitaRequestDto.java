package com.clinica.citas.dto.cita;

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
public class ReprogramarCitaRequestDto {

    @NotNull(message = "La nueva fecha de la cita es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La nueva hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La nueva hora de fin es obligatoria")
    private LocalTime horaFin;

    @Size(max = 300, message = "El motivo no debe exceder 300 caracteres")
    private String motivo;

    @Size(max = 500, message = "Las observaciones no deben exceder 500 caracteres")
    private String observaciones;
}
