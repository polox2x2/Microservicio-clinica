package com.clinica.ecommerce.service.ecommerce;

import com.clinica.ecommerce.client.UsuarioClient;
import com.clinica.ecommerce.client.UsuarioClientDto;
import com.clinica.ecommerce.dto.ecommerce.DetalleOrdenRequestDto;
import com.clinica.ecommerce.dto.ecommerce.OrdenCompraRequestDto;
import com.clinica.ecommerce.dto.ecommerce.OrdenCompraResponseDto;
import com.clinica.ecommerce.exception.BusinessRuleException;
import com.clinica.ecommerce.exception.ResourceNotFoundException;
import com.clinica.ecommerce.mapper.ecommerce.OrdenCompraMapper;
import com.clinica.ecommerce.model.ecommerce.DetalleOrden;
import com.clinica.ecommerce.model.ecommerce.EstadoOrden;
import com.clinica.ecommerce.model.ecommerce.OrdenCompra;
import com.clinica.ecommerce.model.ecommerce.ProductoClinico;
import com.clinica.ecommerce.repository.ecommerce.OrdenCompraRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Lógica de negocio de órdenes
@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoClinicoService productoClinicoService;
    private final UsuarioClient usuarioClient;
    private final OrdenCompraMapper ordenCompraMapper;

    @Transactional(readOnly = true)
    public List<OrdenCompraResponseDto> listarTodas() {
        return ordenCompraRepository.findAll().stream()
                .map(ordenCompraMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrdenCompraResponseDto obtenerPorId(Long id) {
        return ordenCompraMapper.toDto(obtenerEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraResponseDto> listarPorUsuario(Long usuarioId) {
        return ordenCompraRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(ordenCompraMapper::toDto)
                .toList();
    }

    @Transactional
    public OrdenCompraResponseDto crearOrden(OrdenCompraRequestDto request) {
        UsuarioClientDto cliente = validarClienteRemoto(request.getUsuarioId());

        OrdenCompra orden = OrdenCompra.builder()
                .codigoOrden("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .usuarioId(cliente.getId())
                .nombreCliente(cliente.getNombres() + " " + cliente.getApellidos())
                .emailCliente(cliente.getEmail())
                .estado(EstadoOrden.PENDIENTE)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleOrdenRequestDto item : request.getItems()) {
            ProductoClinico producto = productoClinicoService.obtenerEntidad(item.getProductoId());
            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new BusinessRuleException("El producto '" + producto.getNombre() + "' no se encuentra activo");
            }
            if (producto.getStock() < item.getCantidad()) {
                throw new BusinessRuleException(
                        "Stock insuficiente para '" + producto.getNombre() + "'. Disponible: " + producto.getStock());
            }

            producto.setStock(producto.getStock() - item.getCantidad());

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            DetalleOrden detalle = DetalleOrden.builder()
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(subtotal)
                    .build();

            orden.agregarDetalle(detalle);
            total = total.add(subtotal);
        }

        orden.setTotal(total);
        return ordenCompraMapper.toDto(ordenCompraRepository.save(orden));
    }

    @Transactional
    public OrdenCompraResponseDto marcarComoPagada(Long id) {
        OrdenCompra orden = obtenerEntidad(id);
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new BusinessRuleException("Solo se pueden marcar como pagadas las órdenes en estado PENDIENTE");
        }
        orden.setEstado(EstadoOrden.PAGADA);
        return ordenCompraMapper.toDto(ordenCompraRepository.save(orden));
    }

    @Transactional
    public OrdenCompraResponseDto actualizarEstado(Long id, EstadoOrden nuevoEstado) {
        OrdenCompra orden = obtenerEntidad(id);
        if (orden.getEstado() == EstadoOrden.CANCELADA && nuevoEstado != EstadoOrden.CANCELADA) {
            throw new BusinessRuleException("No se puede cambiar el estado de una orden cancelada");
        }
        if (orden.getEstado() != EstadoOrden.CANCELADA && nuevoEstado == EstadoOrden.CANCELADA) {
            for (DetalleOrden detalle : orden.getDetalles()) {
                ProductoClinico producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
            }
        }
        orden.setEstado(nuevoEstado);
        return ordenCompraMapper.toDto(ordenCompraRepository.save(orden));
    }

    private OrdenCompra obtenerEntidad(Long id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada con id: " + id));
    }

    private UsuarioClientDto validarClienteRemoto(Long usuarioId) {
        try {
            UsuarioClientDto cliente = usuarioClient.obtenerUsuarioPorId(usuarioId);
            if (cliente == null) {
                throw new ResourceNotFoundException("Cliente no encontrado con id: " + usuarioId);
            }
            if (!Boolean.TRUE.equals(cliente.getActivo())) {
                throw new BusinessRuleException("El cliente con id " + usuarioId + " no se encuentra activo");
            }
            return cliente;
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Cliente no encontrado en microservice-citas con id: " + usuarioId);
        } catch (FeignException ex) {
            throw new BusinessRuleException(
                    "No fue posible validar el cliente en microservice-citas (código " + ex.status() + ")");
        }
    }
}
