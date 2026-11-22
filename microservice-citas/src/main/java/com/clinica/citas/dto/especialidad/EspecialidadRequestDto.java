package com.clinica.citas.dto.especialidad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos de entrada para especialidades
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspecialidadRequestDto {

    @NotBlank(message = "El nombre de la especialidad es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String nombre;

    @Size(max = 300, message = "La descripción no debe exceder 300 caracteres")
    private String descripcion;

    private Boolean activo;
}
