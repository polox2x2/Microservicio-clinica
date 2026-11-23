package com.clinica.notificaciones.mapper.notificacion;

import com.clinica.notificaciones.dto.notificacion.NotificacionRequestDto;
import com.clinica.notificaciones.dto.notificacion.NotificacionResponseDto;
import com.clinica.notificaciones.model.notificacion.Notificacion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

//mapea entidad y dtos de notificacion
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificacionMapper {

    Notificacion toEntity(NotificacionRequestDto dto);

    NotificacionResponseDto toDto(Notificacion entity);
}
