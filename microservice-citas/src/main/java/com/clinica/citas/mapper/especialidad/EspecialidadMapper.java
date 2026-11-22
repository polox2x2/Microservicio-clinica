package com.clinica.citas.mapper.especialidad;

import com.clinica.citas.dto.especialidad.EspecialidadRequestDto;
import com.clinica.citas.dto.especialidad.EspecialidadResponseDto;
import com.clinica.citas.model.especialidad.Especialidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

// Mapper MapStruct para especialidades
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EspecialidadMapper {

    Especialidad toEntity(EspecialidadRequestDto dto);

    EspecialidadResponseDto toDto(Especialidad entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(EspecialidadRequestDto dto, @MappingTarget Especialidad entity);
}
