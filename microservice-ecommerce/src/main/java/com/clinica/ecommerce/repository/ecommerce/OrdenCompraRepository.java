package com.clinica.ecommerce.repository.ecommerce;

import com.clinica.ecommerce.model.ecommerce.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repositorio JPA de órdenes
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    Optional<OrdenCompra> findByCodigoOrden(String codigoOrden);

    List<OrdenCompra> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
}
