package com.clinica.ecommerce.controller.ecommerce;

import com.clinica.ecommerce.dto.ecommerce.OrdenCompraRequestDto;
import com.clinica.ecommerce.dto.ecommerce.OrdenCompraResponseDto;
import com.clinica.ecommerce.service.ecommerce.OrdenCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//expone endpoints de ordenes de compra
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

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<OrdenCompraResponseDto>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ordenCompraService.listarPorCliente(clienteId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrdenCompraResponseDto>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenCompraService.listarPorCliente(usuarioId));
    }

    @PostMapping
    public ResponseEntity<OrdenCompraResponseDto> crearOrden(@Valid @RequestBody OrdenCompraRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenCompraService.crearOrden(request));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<OrdenCompraResponseDto> marcarComoPagada(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.marcarComoPagada(id));
    }

    @PatchMapping("/{id}/entregar")
    public ResponseEntity<OrdenCompraResponseDto> entregarOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.entregarOrden(id));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<OrdenCompraResponseDto> anularOrden(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.anularOrden(id));
    }
}
