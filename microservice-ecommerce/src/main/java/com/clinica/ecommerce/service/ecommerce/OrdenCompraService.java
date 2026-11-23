package com.clinica.ecommerce.service.ecommerce;

import com.clinica.ecommerce.client.NotificacionClient;
import com.clinica.ecommerce.client.NotificacionClientRequestDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

//gestiona ordenes de compra con validacion remota y control transaccional de stock
@Slf4j
@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProductoClinicoService productoClinicoService;
    private final UsuarioClient usuarioClient;
    private final NotificacionClient notificacionClient;
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
    public List<OrdenCompraResponseDto> listarPorCliente(Long clienteId) {
        return ordenCompraRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId).stream()
                .map(ordenCompraMapper::toDto)
                .toList();
    }

    @Transactional
    public OrdenCompraResponseDto crearOrden(OrdenCompraRequestDto request) {
        UsuarioClientDto cliente = validarClienteRemoto(request.getClienteId());

        OrdenCompra orden = OrdenCompra.builder()
                .codigoOrden("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .clienteId(cliente.getId())
                .nombreCliente(cliente.getNombres() + " " + cliente.getApellidos())
                .emailCliente(cliente.getEmail())
                .telefonoCliente(cliente.getTelefono())
                .estado(EstadoOrden.PENDIENTE)
                .total(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .build();

        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (DetalleOrdenRequestDto item : request.getItems()) {
            ProductoClinico producto = productoClinicoService.obtenerEntidad(item.getProductoId());
            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new BusinessRuleException("El producto '" + producto.getNombre() + "' no se encuentra activo");
            }
            if (producto.getStock() < item.getCantidad()) {
                throw new BusinessRuleException(
                        "Stock insuficiente para '" + producto.getNombre() + "'. Disponible: " + producto.getStock());
            }

            //descuenta stock dentro de la misma transaccion acid
            producto.setStock(producto.getStock() - item.getCantidad());

            BigDecimal precioUnitario = producto.getPrecio().setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            DetalleOrden detalle = DetalleOrden.builder()
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotal)
                    .build();

            orden.agregarDetalle(detalle);
            total = total.add(subtotal);
        }

        orden.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        OrdenCompra guardada = ordenCompraRepository.save(orden);

        for (DetalleOrden detalle : guardada.getDetalles()) {
            productoClinicoService.verificarYNotificarStockBajo(detalle.getProducto());
        }

        return ordenCompraMapper.toDto(guardada);
    }

    @Transactional
    public OrdenCompraResponseDto marcarComoPagada(Long id) {
        OrdenCompra orden = obtenerEntidad(id);
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new BusinessRuleException("Solo se pueden pagar órdenes en estado PENDIENTE");
        }
        orden.setEstado(EstadoOrden.PAGADA);
        OrdenCompra guardada = ordenCompraRepository.save(orden);
        notificarPagoOrden(guardada);
        return ordenCompraMapper.toDto(guardada);
    }

    @Transactional
    public OrdenCompraResponseDto entregarOrden(Long id) {
        OrdenCompra orden = obtenerEntidad(id);
        if (orden.getEstado() != EstadoOrden.PAGADA) {
            throw new BusinessRuleException("Solo se pueden entregar órdenes en estado PAGADA");
        }
        orden.setEstado(EstadoOrden.ENTREGADA);
        return ordenCompraMapper.toDto(ordenCompraRepository.save(orden));
    }

    @Transactional
    public OrdenCompraResponseDto anularOrden(Long id) {
        OrdenCompra orden = obtenerEntidad(id);
        if (orden.getEstado() == EstadoOrden.ANULADA) {
            throw new BusinessRuleException("La orden ya se encuentra anulada");
        }
        if (orden.getEstado() == EstadoOrden.ENTREGADA) {
            throw new BusinessRuleException("No se puede anular una orden que ya fue entregada");
        }

        //devuelve automaticamente al inventario el stock reservado en la orden
        for (DetalleOrden detalle : orden.getDetalles()) {
            ProductoClinico producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }

        orden.setEstado(EstadoOrden.ANULADA);
        return ordenCompraMapper.toDto(ordenCompraRepository.save(orden));
    }

    //notifica confirmacion de pago de forma tolerante a fallos
    private void notificarPagoOrden(OrdenCompra orden) {
        try {
            boolean tieneTelefono = orden.getTelefonoCliente() != null && !orden.getTelefonoCliente().isBlank();
            String canal = tieneTelefono ? "WHATSAPP" : "EMAIL";
            String contacto = tieneTelefono ? orden.getTelefonoCliente() : orden.getEmailCliente();

            NotificacionClientRequestDto payload = NotificacionClientRequestDto.builder()
                    .destinatarioId(orden.getClienteId())
                    .destinatarioNombre(orden.getNombreCliente())
                    .destinatarioContacto(contacto)
                    .canal(canal)
                    .tipoEvento("PAGO_ORDEN")
                    .referenciaId(orden.getId())
                    .asunto("Pago Confirmado - Orden " + orden.getCodigoOrden())
                    .mensaje(String.format("Estimado(a) %s, confirmamos el pago de su orden %s por S/ %s.",
                            orden.getNombreCliente(), orden.getCodigoOrden(), orden.getTotal()))
                    .enviarInmediatamente(Boolean.TRUE)
                    .build();

            notificacionClient.enviarNotificacion(payload);
        } catch (Exception ex) {
            log.warn("No se pudo enviar notificación de pago para la orden {}: {}",
                    orden.getCodigoOrden(), ex.getMessage());
        }
    }

    private OrdenCompra obtenerEntidad(Long id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada con id: " + id));
    }

    private UsuarioClientDto validarClienteRemoto(Long clienteId) {
        try {
            UsuarioClientDto cliente = usuarioClient.obtenerUsuarioPorId(clienteId);
            if (cliente == null) {
                throw new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId);
            }
            if (!Boolean.TRUE.equals(cliente.getActivo())) {
                throw new BusinessRuleException("El cliente con id " + clienteId + " no se encuentra activo");
            }
            return cliente;
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Cliente no encontrado en microservice-citas con id: " + clienteId);
        } catch (FeignException ex) {
            throw new BusinessRuleException(
                    "No fue posible validar el cliente en microservice-citas (código " + ex.status() + ")");
        }
    }
}
