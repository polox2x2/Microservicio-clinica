package com.clinica.citas.controller.especialidad;

import com.clinica.citas.dto.especialidad.EspecialidadRequestDto;
import com.clinica.citas.dto.especialidad.EspecialidadResponseDto;
import com.clinica.citas.service.especialidad.EspecialidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//expone endpoints de especialidades medicas
@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadService especialidadService;

    @GetMapping
    public ResponseEntity<List<EspecialidadResponseDto>> listarTodas() {
        return ResponseEntity.ok(especialidadService.listarTodas());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<EspecialidadResponseDto>> listarActivas() {
        return ResponseEntity.ok(especialidadService.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(especialidadService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<EspecialidadResponseDto> crear(@Valid @RequestBody EspecialidadRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(especialidadService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EspecialidadRequestDto request) {
        return ResponseEntity.ok(especialidadService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EspecialidadResponseDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        return ResponseEntity.ok(especialidadService.cambiarEstado(id, activo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        especialidadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
