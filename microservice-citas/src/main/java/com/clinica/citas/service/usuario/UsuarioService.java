package com.clinica.citas.service.usuario;

import com.clinica.citas.dto.usuario.UsuarioRequestDto;
import com.clinica.citas.dto.usuario.UsuarioResponseDto;
import com.clinica.citas.exception.BusinessRuleException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.mapper.usuario.UsuarioMapper;
import com.clinica.citas.model.especialidad.Especialidad;
import com.clinica.citas.model.usuario.RolUsuario;
import com.clinica.citas.model.usuario.Usuario;
import com.clinica.citas.repository.usuario.UsuarioRepository;
import com.clinica.citas.service.especialidad.EspecialidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Lógica de negocio de usuarios
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final String DEFAULT_LOCAL_PASSWORD = "123456";

    private final UsuarioRepository usuarioRepository;
    private final EspecialidadService especialidadService;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarDoctoresActivosPorEspecialidad(Long especialidadId) {
        return usuarioRepository.findByRolAndEspecialidadIdAndActivoTrue(RolUsuario.DOCTOR, especialidadId).stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDto obtenerPorId(Long id) {
        return usuarioMapper.toDto(obtenerEntidad(id));
    }

    @Transactional
    public UsuarioResponseDto crear(UsuarioRequestDto request) {
        validarUnicidad(request.getDni().trim(), request.getEmail().trim(), null);
        Especialidad especialidad = resolverEspecialidadParaRol(request.getRol(), request.getEspecialidadId());

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setDni(request.getDni().trim());
        usuario.setEmail(request.getEmail().trim().toLowerCase());
        usuario.setEspecialidad(especialidad);
        String rawPassword = (request.getPassword() != null && !request.getPassword().isBlank())
                ? request.getPassword()
                : DEFAULT_LOCAL_PASSWORD;
        usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
        if (usuario.getActivo() == null) {
            usuario.setActivo(Boolean.TRUE);
        }

        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDto actualizar(Long id, UsuarioRequestDto request) {
        Usuario usuario = obtenerEntidad(id);
        validarUnicidad(request.getDni().trim(), request.getEmail().trim(), id);
        Especialidad especialidad = resolverEspecialidadParaRol(request.getRol(), request.getEspecialidadId());

        usuarioMapper.updateEntity(request, usuario);
        usuario.setDni(request.getDni().trim());
        usuario.setEmail(request.getEmail().trim().toLowerCase());
        usuario.setEspecialidad(especialidad);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDto cambiarEstado(Long id, boolean activo) {
        Usuario usuario = obtenerEntidad(id);
        usuario.setActivo(activo);
        return usuarioMapper.toDto(usuarioRepository.save(usuario));
    }

    public Usuario obtenerEntidad(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    private void validarUnicidad(String dni, String email, Long idActual) {
        boolean dniDuplicado = idActual == null
                ? usuarioRepository.existsByDni(dni)
                : usuarioRepository.existsByDniAndIdNot(dni, idActual);
        if (dniDuplicado) {
            throw new BusinessRuleException("Ya existe un usuario registrado con el DNI: " + dni);
        }

        boolean emailDuplicado = idActual == null
                ? usuarioRepository.existsByEmailIgnoreCase(email)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, idActual);
        if (emailDuplicado) {
            throw new BusinessRuleException("Ya existe un usuario registrado con el email: " + email);
        }
    }

    private Especialidad resolverEspecialidadParaRol(RolUsuario rol, Long especialidadId) {
        if (rol == RolUsuario.DOCTOR) {
            if (especialidadId == null) {
                throw new BusinessRuleException("Un usuario con rol DOCTOR debe tener una especialidadId válida");
            }
            Especialidad especialidad = especialidadService.obtenerEntidad(especialidadId);
            if (!Boolean.TRUE.equals(especialidad.getActivo())) {
                throw new BusinessRuleException("La especialidad seleccionada no se encuentra activa");
            }
            return especialidad;
        }
        if (especialidadId != null) {
            throw new BusinessRuleException("Solo los usuarios con rol DOCTOR pueden tener especialidad asignada");
        }
        return null;
    }
}
