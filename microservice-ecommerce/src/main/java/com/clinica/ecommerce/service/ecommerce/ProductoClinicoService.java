package com.clinica.ecommerce.service.ecommerce;

import com.clinica.ecommerce.dto.ecommerce.ProductoClinicoRequestDto;
import com.clinica.ecommerce.dto.ecommerce.ProductoClinicoResponseDto;
import com.clinica.ecommerce.exception.BusinessRuleException;
import com.clinica.ecommerce.exception.ResourceNotFoundException;
import com.clinica.ecommerce.mapper.ecommerce.ProductoClinicoMapper;
import com.clinica.ecommerce.model.ecommerce.ProductoClinico;
import com.clinica.ecommerce.repository.ecommerce.ProductoClinicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Lógica de negocio de productos
@Service
@RequiredArgsConstructor
public class ProductoClinicoService {

    private final ProductoClinicoRepository productoClinicoRepository;
    private final ProductoClinicoMapper productoClinicoMapper;

    @Transactional(readOnly = true)
    public List<ProductoClinicoResponseDto> listarTodos() {
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
        return productoClinicoRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria).stream()
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
        if (producto.getRequiereReceta() == null) {
            producto.setRequiereReceta(Boolean.FALSE);
        }
        if (producto.getActivo() == null) {
            producto.setActivo(Boolean.TRUE);
        }

        return productoClinicoMapper.toDto(productoClinicoRepository.save(producto));
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

        return productoClinicoMapper.toDto(productoClinicoRepository.save(producto));
    }

    @Transactional
    public ProductoClinicoResponseDto ajustarStock(Long id, int delta) {
        ProductoClinico producto = obtenerEntidad(id);
        int nuevoStock = producto.getStock() + delta;
        if (nuevoStock < 0) {
            throw new BusinessRuleException("El ajuste dejaría stock negativo para el producto " + producto.getNombre());
        }
        producto.setStock(nuevoStock);
        return productoClinicoMapper.toDto(productoClinicoRepository.save(producto));
    }

    public ProductoClinico obtenerEntidad(Long id) {
        return productoClinicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto clínico no encontrado con id: " + id));
    }
}
