package com.clinica.citas.controller.cita;

import com.clinica.citas.dto.cita.CitaRequestDto;
import com.clinica.citas.dto.cita.CitaResponseDto;
import com.clinica.citas.model.cita.EstadoCita;
import com.clinica.citas.service.cita.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Endpoints REST para citas médicas
@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    public ResponseEntity<List<CitaResponseDto>> listarTodas() {
        return ResponseEntity.ok(citaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<CitaResponseDto>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(citaService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<CitaResponseDto>> listarPorDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(citaService.listarPorDoctor(doctorId));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<CitaResponseDto>> listarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.listarPorFecha(fecha));
    }

    @PostMapping
    public ResponseEntity<CitaResponseDto> reservar(@Valid @RequestBody CitaRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.reservar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequestDto request) {
        return ResponseEntity.ok(citaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<CitaResponseDto> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.confirmar(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CitaResponseDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoCita estado) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, estado));
    }
}
