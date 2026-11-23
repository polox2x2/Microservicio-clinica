package com.clinica.citas.dto.cita;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtenderCitaRequestDto {

    @NotBlank(message = "Las notas clínicas son obligatorias")
    @Size(max = 1000, message = "Las notas clínicas no deben exceder 1000 caracteres")
    private String notasClinicas;

    @Size(max = 500, message = "Las observaciones no deben exceder 500 caracteres")
    private String observaciones;
}
