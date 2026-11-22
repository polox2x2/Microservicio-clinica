package com.clinica.ecommerce.dto.ecommerce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Datos de salida de ítem de orden
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleOrdenResponseDto {

    private Long id;
    private Long productoId;
    private String productoSku;
    private String productoNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
