package com.clinica.citas.mapper.usuario;

import com.clinica.citas.dto.usuario.UsuarioRequestDto;
import com.clinica.citas.dto.usuario.UsuarioResponseDto;
import com.clinica.citas.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

// Mapper MapStruct para usuarios
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UsuarioMapper {

    @Mapping(target = "especialidad", ignore = true)
    Usuario toEntity(UsuarioRequestDto dto);

    @Mapping(target = "especialidadId", source = "especialidad.id")
    @Mapping(target = "especialidadNombre", source = "especialidad.nombre")
    UsuarioResponseDto toDto(Usuario entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "especialidad", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateEntity(UsuarioRequestDto dto, @MappingTarget Usuario entity);
}
