package com.clinica.citas.repository.especialidad;

import com.clinica.citas.model.especialidad.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio JPA de especialidades
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Especialidad> findByActivoTrue();
}
