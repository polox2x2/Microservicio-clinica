package com.clinica.citas.controller.horario;

import com.clinica.citas.dto.horario.CupoDisponibleResponseDto;
import com.clinica.citas.dto.horario.HorarioDoctorRequestDto;
import com.clinica.citas.dto.horario.HorarioDoctorResponseDto;
import com.clinica.citas.service.horario.HorarioDoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

//expone endpoints de horarios medicos y disponibilidad
@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioDoctorController {

    private final HorarioDoctorService horarioDoctorService;

    @GetMapping
    public ResponseEntity<List<HorarioDoctorResponseDto>> listarTodos() {
        return ResponseEntity.ok(horarioDoctorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioDoctorResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioDoctorService.obtenerPorId(id));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<HorarioDoctorResponseDto>> listarPorDoctor(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "false") boolean soloActivos) {
        return ResponseEntity.ok(horarioDoctorService.listarPorDoctor(doctorId, soloActivos));
    }

    @GetMapping("/doctor/{doctorId}/disponibilidad")
    public ResponseEntity<List<CupoDisponibleResponseDto>> consultarDisponibilidad(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(horarioDoctorService.consultarDisponibilidad(doctorId, fecha));
    }

    @PostMapping
    public ResponseEntity<HorarioDoctorResponseDto> crear(@Valid @RequestBody HorarioDoctorRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioDoctorService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorarioDoctorResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody HorarioDoctorRequestDto request) {
        return ResponseEntity.ok(horarioDoctorService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<HorarioDoctorResponseDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        return ResponseEntity.ok(horarioDoctorService.cambiarEstado(id, activo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioDoctorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
