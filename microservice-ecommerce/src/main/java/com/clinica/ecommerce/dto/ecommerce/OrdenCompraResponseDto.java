package com.clinica.ecommerce.dto.ecommerce;

import com.clinica.ecommerce.model.ecommerce.EstadoOrden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Datos de salida de una orden
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraResponseDto {

    private Long id;
    private String codigoOrden;
    private Long usuarioId;
    private String nombreCliente;
    private String emailCliente;
    private BigDecimal total;
    private EstadoOrden estado;
    private LocalDateTime fechaCreacion;
    private List<DetalleOrdenResponseDto> detalles;
}
