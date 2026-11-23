package com.clinica.citas.dto.especialidad;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    @NotNull(message = "La tarifa base es obligatoria")
    @DecimalMin(value = "0.01", message = "La tarifa base debe ser mayor a cero")
    private BigDecimal tarifaBase;

    @NotNull(message = "La duración estándar es obligatoria")
    @Min(value = 10, message = "La duración estándar mínima es 10 minutos")
    @Max(value = 240, message = "La duración estándar máxima es 240 minutos")
    private Integer duracionEstandarMinutos;

    private Boolean activo;
}
