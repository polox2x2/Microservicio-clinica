package com.clinica.citas.service.auth;

import com.clinica.citas.config.JwtService;
import com.clinica.citas.dto.auth.LoginRequest;
import com.clinica.citas.dto.auth.TokenResponse;
import com.clinica.citas.exception.BusinessRuleException;
import com.clinica.citas.model.usuario.Usuario;
import com.clinica.citas.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Autentica usuarios y emite JWT
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BusinessRuleException("El usuario se encuentra inactivo");
        }

        String token = jwtService.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().name()
        );

        return TokenResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInMs(jwtService.getExpirationMs())
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }
}
