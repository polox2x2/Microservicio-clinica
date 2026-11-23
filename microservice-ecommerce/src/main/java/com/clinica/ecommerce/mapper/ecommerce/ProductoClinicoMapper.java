package com.clinica.ecommerce.mapper.ecommerce;

import com.clinica.ecommerce.dto.ecommerce.ProductoClinicoRequestDto;
import com.clinica.ecommerce.dto.ecommerce.ProductoClinicoResponseDto;
import com.clinica.ecommerce.model.ecommerce.ProductoClinico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

//mapea entidad y dtos de producto clinico
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductoClinicoMapper {

    ProductoClinico toEntity(ProductoClinicoRequestDto dto);

    ProductoClinicoResponseDto toDto(ProductoClinico entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    void updateEntity(ProductoClinicoRequestDto dto, @MappingTarget ProductoClinico entity);
}
