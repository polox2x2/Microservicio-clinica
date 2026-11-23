package com.clinica.ecommerce.mapper.ecommerce;

import com.clinica.ecommerce.dto.ecommerce.DetalleOrdenResponseDto;
import com.clinica.ecommerce.dto.ecommerce.OrdenCompraResponseDto;
import com.clinica.ecommerce.model.ecommerce.DetalleOrden;
import com.clinica.ecommerce.model.ecommerce.OrdenCompra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

//mapea entidad y dtos de orden de compra
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrdenCompraMapper {

    OrdenCompraResponseDto toDto(OrdenCompra entity);

    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoSku", source = "producto.sku")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    DetalleOrdenResponseDto toDetalleDto(DetalleOrden detalle);
}
