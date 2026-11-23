package com.clinica.ecommerce.dto.ecommerce;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraRequestDto {

    @JsonAlias("usuarioId")
    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;

    @Valid
    @NotEmpty(message = "La orden debe incluir al menos un producto")
    private List<DetalleOrdenRequestDto> items;
}
