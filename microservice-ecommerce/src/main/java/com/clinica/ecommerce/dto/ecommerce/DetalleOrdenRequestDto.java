package com.clinica.ecommerce.dto.ecommerce;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Datos de entrada para ítem de orden
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleOrdenRequestDto {

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima por producto es 1")
    private Integer cantidad;
}
