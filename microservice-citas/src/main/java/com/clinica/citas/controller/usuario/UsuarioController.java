package com.clinica.citas.controller.usuario;

import com.clinica.citas.dto.usuario.UsuarioRequestDto;
import com.clinica.citas.dto.usuario.UsuarioResponseDto;
import com.clinica.citas.model.usuario.RolUsuario;
import com.clinica.citas.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//expone endpoints de gestion de usuarios clinicos
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDto>> listarPorRol(@PathVariable RolUsuario rol) {
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @GetMapping("/doctores")
    public ResponseEntity<List<UsuarioResponseDto>> listarDoctoresActivos(
            @RequestParam(required = false) Long especialidadId) {
        return ResponseEntity.ok(usuarioService.listarDoctoresActivos(especialidadId));
    }

    @GetMapping("/doctores/especialidad/{especialidadId}")
    public ResponseEntity<List<UsuarioResponseDto>> listarDoctoresPorEspecialidad(@PathVariable Long especialidadId) {
        return ResponseEntity.ok(usuarioService.listarDoctoresActivos(especialidadId));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody UsuarioRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, activo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.desactivar(id));
    }
}
