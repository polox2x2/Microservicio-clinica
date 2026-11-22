package com.clinica.ecommerce.dto.ecommerce;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Datos de entrada para productos clínicos
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoClinicoRequestDto {

    @NotBlank(message = "El SKU del producto es obligatorio")
    @Size(max = 50, message = "El SKU no debe exceder 50 caracteres")
    private String sku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 80, message = "La categoría no debe exceder 80 caracteres")
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private Boolean requiereReceta;

    private Boolean activo;
}
