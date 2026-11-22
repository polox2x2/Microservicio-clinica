package com.clinica.ecommerce.dto.ecommerce;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Datos de entrada para orden de compra
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraRequestDto {

    @NotNull(message = "El usuarioId del cliente es obligatorio")
    private Long usuarioId;

    @Valid
    @NotEmpty(message = "La orden debe incluir al menos un producto")
    private List<DetalleOrdenRequestDto> items;
}
