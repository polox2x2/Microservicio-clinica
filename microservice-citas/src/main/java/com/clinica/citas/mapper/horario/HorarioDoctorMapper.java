package com.clinica.citas.mapper.horario;

import com.clinica.citas.dto.horario.HorarioDoctorRequestDto;
import com.clinica.citas.dto.horario.HorarioDoctorResponseDto;
import com.clinica.citas.model.horario.HorarioDoctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

//mapea entidad y dtos de horario medico
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface HorarioDoctorMapper {

    @Mapping(target = "doctor", ignore = true)
    HorarioDoctor toEntity(HorarioDoctorRequestDto dto);

    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(
            target = "doctorNombre",
            expression = "java(entity.getDoctor() != null ? entity.getDoctor().getNombres() + \" \" + entity.getDoctor().getApellidos() : null)"
    )
    HorarioDoctorResponseDto toDto(HorarioDoctor entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    void updateEntity(HorarioDoctorRequestDto dto, @MappingTarget HorarioDoctor entity);
}
