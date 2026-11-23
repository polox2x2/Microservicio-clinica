package com.clinica.citas.dto.especialidad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspecialidadResponseDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal tarifaBase;
    private Integer duracionEstandarMinutos;
    private Boolean activo;
}
