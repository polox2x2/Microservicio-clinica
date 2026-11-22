package com.clinica.citas.dto.especialidad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos de salida de una especialidad
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspecialidadResponseDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}
