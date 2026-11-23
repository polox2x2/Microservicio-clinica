package com.clinica.ecommerce.service.ecommerce;

import com.clinica.ecommerce.client.NotificacionClient;
import com.clinica.ecommerce.client.NotificacionClientRequestDto;
import com.clinica.ecommerce.dto.ecommerce.ProductoClinicoRequestDto;
import com.clinica.ecommerce.dto.ecommerce.ProductoClinicoResponseDto;
import com.clinica.ecommerce.exception.BusinessRuleException;
import com.clinica.ecommerce.exception.ResourceNotFoundException;
import com.clinica.ecommerce.mapper.ecommerce.ProductoClinicoMapper;
import com.clinica.ecommerce.model.ecommerce.ProductoClinico;
import com.clinica.ecommerce.repository.ecommerce.ProductoClinicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;

//gestiona catalogo de productos clinicos y alertas de stock
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoClinicoService {

    private final ProductoClinicoRepository productoClinicoRepository;
    private final ProductoClinicoMapper productoClinicoMapper;
    private final NotificacionClient notificacionClient;

    @Transactional(readOnly = true)
    public List<ProductoClinicoResponseDto> listarTodos(String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return listarPorCategoria(categoria);
        }
        return productoClinicoRepository.findAll().stream()
                .map(productoClinicoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoClinicoResponseDto> listarActivos() {
        return productoClinicoRepository.findByActivoTrue().stream()
                .map(productoClinicoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoClinicoResponseDto> listarPorCategoria(String categoria) {
        return productoClinicoRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria.trim()).stream()
                .map(productoClinicoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoClinicoResponseDto> listarStockBajo() {
        return productoClinicoRepository.findProductosConStockBajo().stream()
                .map(productoClinicoMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoClinicoResponseDto obtenerPorId(Long id) {
        return productoClinicoMapper.toDto(obtenerEntidad(id));
    }

    @Transactional
    public ProductoClinicoResponseDto crear(ProductoClinicoRequestDto request) {
        String skuNormalizado = request.getSku().trim().toUpperCase();
        if (productoClinicoRepository.existsBySkuIgnoreCase(skuNormalizado)) {
            throw new BusinessRuleException("Ya existe un producto con el SKU: " + skuNormalizado);
        }

        ProductoClinico producto = productoClinicoMapper.toEntity(request);
        producto.setSku(skuNormalizado);
        producto.setNombre(request.getNombre().trim());
        producto.setCategoria(request.getCategoria().trim());
        producto.setPrecio(request.getPrecio().setScale(2, RoundingMode.HALF_UP));
        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(5);
        }
        if (producto.getRequiereReceta() == null) {
            producto.setRequiereReceta(Boolean.FALSE);
        }
        if (producto.getActivo() == null) {
            producto.setActivo(Boolean.TRUE);
        }

        ProductoClinico guardado = productoClinicoRepository.save(producto);
        verificarYNotificarStockBajo(guardado);
        return productoClinicoMapper.toDto(guardado);
    }

    @Transactional
    public ProductoClinicoResponseDto actualizar(Long id, ProductoClinicoRequestDto request) {
        ProductoClinico producto = obtenerEntidad(id);
        String skuNormalizado = request.getSku().trim().toUpperCase();
        if (productoClinicoRepository.existsBySkuIgnoreCaseAndIdNot(skuNormalizado, id)) {
            throw new BusinessRuleException("Ya existe otro producto con el SKU: " + skuNormalizado);
        }

        productoClinicoMapper.updateEntity(request, producto);
        producto.setSku(skuNormalizado);
        producto.setNombre(request.getNombre().trim());
        producto.setCategoria(request.getCategoria().trim());
        producto.setPrecio(request.getPrecio().setScale(2, RoundingMode.HALF_UP));

        ProductoClinico guardado = productoClinicoRepository.save(producto);
        verificarYNotificarStockBajo(guardado);
        return productoClinicoMapper.toDto(guardado);
    }

    @Transactional
    public ProductoClinicoResponseDto ajustarStock(Long id, int delta) {
        ProductoClinico producto = obtenerEntidad(id);
        int nuevoStock = producto.getStock() + delta;
        if (nuevoStock < 0) {
            throw new BusinessRuleException(
                    "El ajuste dejaría stock negativo para el producto " + producto.getNombre());
        }
        producto.setStock(nuevoStock);
        ProductoClinico guardado = productoClinicoRepository.save(producto);
        verificarYNotificarStockBajo(guardado);
        return productoClinicoMapper.toDto(guardado);
    }

    @Transactional
    public ProductoClinicoResponseDto cambiarEstado(Long id, boolean activo) {
        ProductoClinico producto = obtenerEntidad(id);
        producto.setActivo(activo);
        return productoClinicoMapper.toDto(productoClinicoRepository.save(producto));
    }

    public ProductoClinico obtenerEntidad(Long id) {
        return productoClinicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto clínico no encontrado con id: " + id));
    }

    //emite alerta de stock tolerante a fallos cuando alcanza o baja del minimo
    public void verificarYNotificarStockBajo(ProductoClinico producto) {
        if (!Boolean.TRUE.equals(producto.getActivo()) || producto.getStock() > producto.getStockMinimo()) {
            return;
        }
        try {
            NotificacionClientRequestDto payload = NotificacionClientRequestDto.builder()
                    .destinatarioId(1L)
                    .destinatarioNombre("Administración Farmacia Clínica")
                    .destinatarioContacto("admin@clinica.pe")
                    .canal("EMAIL")
                    .tipoEvento("ALERTA_STOCK")
                    .referenciaId(producto.getId())
                    .asunto("Alerta de Stock Bajo: " + producto.getSku())
                    .mensaje(String.format("El producto %s (%s) registra stock de %d unidades (mínimo: %d).",
                            producto.getNombre(), producto.getSku(), producto.getStock(), producto.getStockMinimo()))
                    .enviarInmediatamente(Boolean.TRUE)
                    .build();
            notificacionClient.enviarNotificacion(payload);
        } catch (Exception ex) {
            log.warn("No se pudo enviar alerta de stock para {}: {}", producto.getSku(), ex.getMessage());
        }
    }
}
