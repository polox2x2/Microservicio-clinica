package com.clinica.notificaciones.controller.notificacion;

import com.clinica.notificaciones.dto.notificacion.NotificacionRequestDto;
import com.clinica.notificaciones.dto.notificacion.NotificacionResponseDto;
import com.clinica.notificaciones.model.notificacion.EstadoNotificacion;
import com.clinica.notificaciones.service.notificacion.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//expone endpoints de notificaciones y recordatorios clinicos
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDto>> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.obtenerPorId(id));
    }

    @GetMapping("/destinatario/{destinatarioId}")
    public ResponseEntity<List<NotificacionResponseDto>> listarPorDestinatarioId(@PathVariable Long destinatarioId) {
        return ResponseEntity.ok(notificacionService.listarPorDestinatarioId(destinatarioId));
    }

    @GetMapping("/destinatario")
    public ResponseEntity<List<NotificacionResponseDto>> listarPorContacto(@RequestParam String contacto) {
        return ResponseEntity.ok(notificacionService.listarPorContacto(contacto));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<NotificacionResponseDto>> listarPendientes() {
        return ResponseEntity.ok(notificacionService.listarPendientes());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<NotificacionResponseDto>> listarPorEstado(@PathVariable EstadoNotificacion estado) {
        return ResponseEntity.ok(notificacionService.listarPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<NotificacionResponseDto> programarOEnviar(
            @Valid @RequestBody NotificacionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.programarOEnviar(request));
    }

    @PatchMapping("/{id}/enviar")
    public ResponseEntity<NotificacionResponseDto> marcarComoEnviada(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoEnviada(id));
    }

    @PatchMapping("/{id}/fallida")
    public ResponseEntity<NotificacionResponseDto> marcarComoFallida(
            @PathVariable Long id,
            @RequestParam(defaultValue = "Error en proveedor de mensajería") String detalleError) {
        return ResponseEntity.ok(notificacionService.marcarComoFallida(id, detalleError));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<NotificacionResponseDto> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.cancelar(id));
    }

    @PostMapping("/procesar-pendientes")
    public ResponseEntity<List<NotificacionResponseDto>> procesarPendientesVencidas() {
        return ResponseEntity.ok(notificacionService.procesarPendientesVencidas());
    }
}
