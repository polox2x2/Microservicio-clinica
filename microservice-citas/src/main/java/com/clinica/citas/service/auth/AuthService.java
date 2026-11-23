package com.clinica.citas.service.auth;

import com.clinica.citas.config.JwtService;
import com.clinica.citas.dto.auth.LoginRequest;
import com.clinica.citas.dto.auth.RegistroPacienteRequest;
import com.clinica.citas.dto.auth.TokenResponse;
import com.clinica.citas.exception.BusinessRuleException;
import com.clinica.citas.mapper.usuario.UsuarioMapper;
import com.clinica.citas.model.usuario.RolUsuario;
import com.clinica.citas.model.usuario.Usuario;
import com.clinica.citas.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//gestiona registro de pacientes y autenticacion jwt
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public TokenResponse registrarPaciente(RegistroPacienteRequest request) {
        String dniNormalizado = request.getDni().trim();
        String emailNormalizado = request.getEmail().trim().toLowerCase();

        //verifica unicidad de dni y email antes de registrar al paciente
        if (usuarioRepository.existsByDni(dniNormalizado)) {
            throw new BusinessRuleException("Ya existe un usuario registrado con el DNI: " + dniNormalizado);
        }
        if (usuarioRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new BusinessRuleException("Ya existe un usuario registrado con el email: " + emailNormalizado);
        }

        Usuario paciente = usuarioMapper.fromRegistroPaciente(request);
        paciente.setNombres(request.getNombres().trim());
        paciente.setApellidos(request.getApellidos().trim());
        paciente.setDni(dniNormalizado);
        paciente.setEmail(emailNormalizado);
        if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
            paciente.setTelefono(request.getTelefono().trim());
        }
        paciente.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        paciente.setRol(RolUsuario.PACIENTE);
        paciente.setActivo(Boolean.TRUE);

        Usuario guardado = usuarioRepository.save(paciente);
        return construirTokenResponse(guardado);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        //impide emitir token a cuentas desactivadas
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BusinessRuleException("El usuario se encuentra inactivo");
        }

        return construirTokenResponse(usuario);
    }

    private TokenResponse construirTokenResponse(Usuario usuario) {
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
