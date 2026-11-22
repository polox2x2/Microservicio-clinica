package com.clinica.ecommerce.controller.ecommerce;

import com.clinica.ecommerce.dto.ecommerce.OrdenCompraRequestDto;
import com.clinica.ecommerce.dto.ecommerce.OrdenCompraResponseDto;
import com.clinica.ecommerce.model.ecommerce.EstadoOrden;
import com.clinica.ecommerce.service.ecommerce.OrdenCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints REST para órdenes de compra
@RestController
@RequestMapping("/api/ecommerce/ordenes")
@RequiredArgsConstructor
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @GetMapping
    public ResponseEntity<List<OrdenCompraResponseDto>> listarTodas() {
        return ResponseEntity.ok(ordenCompraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrdenCompraResponseDto>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenCompraService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<OrdenCompraResponseDto> crearOrden(@Valid @RequestBody OrdenCompraRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenCompraService.crearOrden(request));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<OrdenCompraResponseDto> marcarComoPagada(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.marcarComoPagada(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenCompraResponseDto> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoOrden estado) {
        return ResponseEntity.ok(ordenCompraService.actualizarEstado(id, estado));
    }
}
