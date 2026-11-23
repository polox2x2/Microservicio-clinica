package com.clinica.ecommerce.dto.ecommerce;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoClinicoResponseDto {

    private Long id;
    private String sku;
    private String nombre;
    private String descripcion;
    private String categoria;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private Boolean requiereReceta;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
