package com.clinica.citas.controller.auth;

import com.clinica.citas.dto.auth.LoginRequest;
import com.clinica.citas.dto.auth.RegistroPacienteRequest;
import com.clinica.citas.dto.auth.TokenResponse;
import com.clinica.citas.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//expone endpoints de registro y login jwt
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<TokenResponse> registrarPaciente(@Valid @RequestBody RegistroPacienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrarPaciente(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
