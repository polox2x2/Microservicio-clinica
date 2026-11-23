package com.clinica.citas.service.especialidad;

import com.clinica.citas.dto.especialidad.EspecialidadRequestDto;
import com.clinica.citas.dto.especialidad.EspecialidadResponseDto;
import com.clinica.citas.exception.BusinessRuleException;
import com.clinica.citas.exception.ResourceNotFoundException;
import com.clinica.citas.mapper.especialidad.EspecialidadMapper;
import com.clinica.citas.model.especialidad.Especialidad;
import com.clinica.citas.repository.especialidad.EspecialidadRepository;
import com.clinica.citas.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//gestiona catalogo de especialidades medicas
@Service
@RequiredArgsConstructor
public class EspecialidadService {

    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialidadMapper especialidadMapper;

    @Transactional(readOnly = true)
    public List<EspecialidadResponseDto> listarTodas() {
        return especialidadRepository.findAll().stream()
                .map(especialidadMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EspecialidadResponseDto> listarActivas() {
        return especialidadRepository.findByActivoTrue().stream()
                .map(especialidadMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EspecialidadResponseDto obtenerPorId(Long id) {
        return especialidadMapper.toDto(obtenerEntidad(id));
    }

    @Transactional
    public EspecialidadResponseDto crear(EspecialidadRequestDto request) {
        String nombreNormalizado = request.getNombre().trim();
        if (especialidadRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new BusinessRuleException("Ya existe una especialidad con el nombre: " + nombreNormalizado);
        }
        Especialidad especialidad = especialidadMapper.toEntity(request);
        especialidad.setNombre(nombreNormalizado);
        if (especialidad.getDuracionEstandarMinutos() == null) {
            especialidad.setDuracionEstandarMinutos(30);
        }
        if (especialidad.getActivo() == null) {
            especialidad.setActivo(Boolean.TRUE);
        }
        return especialidadMapper.toDto(especialidadRepository.save(especialidad));
    }

    @Transactional
    public EspecialidadResponseDto actualizar(Long id, EspecialidadRequestDto request) {
        Especialidad especialidad = obtenerEntidad(id);
        String nombreNormalizado = request.getNombre().trim();
        if (especialidadRepository.existsByNombreIgnoreCaseAndIdNot(nombreNormalizado, id)) {
            throw new BusinessRuleException("Ya existe otra especialidad con el nombre: " + nombreNormalizado);
        }
        especialidadMapper.updateEntity(request, especialidad);
        especialidad.setNombre(nombreNormalizado);
        return especialidadMapper.toDto(especialidadRepository.save(especialidad));
    }

    @Transactional
    public EspecialidadResponseDto cambiarEstado(Long id, boolean activo) {
        Especialidad especialidad = obtenerEntidad(id);
        especialidad.setActivo(activo);
        return especialidadMapper.toDto(especialidadRepository.save(especialidad));
    }

    @Transactional
    public void eliminar(Long id) {
        Especialidad especialidad = obtenerEntidad(id);
        //impide eliminar especialidades asociadas a doctores existentes
        if (usuarioRepository.existsByEspecialidadId(id)) {
            throw new BusinessRuleException("No se puede eliminar la especialidad porque tiene doctores asignados");
        }
        especialidadRepository.delete(especialidad);
    }

    public Especialidad obtenerEntidad(Long id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id: " + id));
    }
}
