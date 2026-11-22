package com.clinica.ecommerce.controller.ecommerce;

import com.clinica.ecommerce.dto.ecommerce.ProductoClinicoRequestDto;
import com.clinica.ecommerce.dto.ecommerce.ProductoClinicoResponseDto;
import com.clinica.ecommerce.service.ecommerce.ProductoClinicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints REST para productos clínicos
@RestController
@RequestMapping("/api/ecommerce/productos")
@RequiredArgsConstructor
public class ProductoClinicoController {

    private final ProductoClinicoService productoClinicoService;

    @GetMapping
    public ResponseEntity<List<ProductoClinicoResponseDto>> listarTodos() {
        return ResponseEntity.ok(productoClinicoService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProductoClinicoResponseDto>> listarActivos() {
        return ResponseEntity.ok(productoClinicoService.listarActivos());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoClinicoResponseDto>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoClinicoService.listarPorCategoria(categoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoClinicoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoClinicoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoClinicoResponseDto> crear(@Valid @RequestBody ProductoClinicoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoClinicoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoClinicoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoClinicoRequestDto request) {
        return ResponseEntity.ok(productoClinicoService.actualizar(id, request));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoClinicoResponseDto> ajustarStock(
            @PathVariable Long id,
            @RequestParam int delta) {
        return ResponseEntity.ok(productoClinicoService.ajustarStock(id, delta));
    }
}
