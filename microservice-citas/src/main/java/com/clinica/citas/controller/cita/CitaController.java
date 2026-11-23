package com.clinica.citas.controller.cita;

import com.clinica.citas.dto.cita.AtenderCitaRequestDto;
import com.clinica.citas.dto.cita.CitaRequestDto;
import com.clinica.citas.dto.cita.CitaResponseDto;
import com.clinica.citas.dto.cita.ReprogramarCitaRequestDto;
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

//expone endpoints de citas medicas
@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    public ResponseEntity<List<CitaResponseDto>> listarTodas() {
        return ResponseEntity.ok(citaService.listarTodas());
    }

    @GetMapping("/historial")
    public ResponseEntity<List<CitaResponseDto>> buscarHistorial(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) EstadoCita estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(citaService.buscarHistorial(pacienteId, doctorId, estado, fechaInicio, fechaFin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<CitaResponseDto>> listarPorPaciente(
            @PathVariable Long pacienteId,
            @RequestParam(required = false) EstadoCita estado) {
        return ResponseEntity.ok(citaService.listarPorPaciente(pacienteId, estado));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<CitaResponseDto>> listarPorDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.listarPorDoctor(doctorId, fecha));
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

    @PutMapping("/{id}/reprogramar")
    public ResponseEntity<CitaResponseDto> reprogramar(
            @PathVariable Long id,
            @Valid @RequestBody ReprogramarCitaRequestDto request) {
        return ResponseEntity.ok(citaService.reprogramar(id, request));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<CitaResponseDto> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.confirmar(id));
    }

    @PatchMapping("/{id}/atender")
    public ResponseEntity<CitaResponseDto> atender(
            @PathVariable Long id,
            @Valid @RequestBody AtenderCitaRequestDto request) {
        return ResponseEntity.ok(citaService.atender(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDto> cancelar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(citaService.cancelar(id, motivo));
    }

    @PatchMapping("/{id}/no-asistio")
    public ResponseEntity<CitaResponseDto> marcarNoAsistio(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.marcarNoAsistio(id));
    }
}
