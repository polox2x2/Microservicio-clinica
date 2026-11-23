package com.clinica.citas.mapper.cita;

import com.clinica.citas.dto.cita.CitaRequestDto;
import com.clinica.citas.dto.cita.CitaResponseDto;
import com.clinica.citas.model.cita.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

//mapea entidad y dtos de cita medica
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CitaMapper {

    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "especialidad", ignore = true)
    Cita toEntity(CitaRequestDto dto);

    @Mapping(target = "pacienteId", source = "paciente.id")
    @Mapping(
            target = "pacienteNombre",
            expression = "java(entity.getPaciente() != null ? entity.getPaciente().getNombres() + \" \" + entity.getPaciente().getApellidos() : null)"
    )
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(
            target = "doctorNombre",
            expression = "java(entity.getDoctor() != null ? entity.getDoctor().getNombres() + \" \" + entity.getDoctor().getApellidos() : null)"
    )
    @Mapping(target = "especialidadId", source = "especialidad.id")
    @Mapping(target = "especialidadNombre", source = "especialidad.nombre")
    CitaResponseDto toDto(Cita entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "especialidad", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateEntity(CitaRequestDto dto, @MappingTarget Cita entity);
}
