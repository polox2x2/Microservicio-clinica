package com.clinica.citas.repository.usuario;

import com.clinica.citas.model.usuario.RolUsuario;
import com.clinica.citas.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repositorio JPA de usuarios
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByEspecialidadId(Long especialidadId);

    List<Usuario> findByRol(RolUsuario rol);

    List<Usuario> findByRolAndEspecialidadIdAndActivoTrue(RolUsuario rol, Long especialidadId);
}
