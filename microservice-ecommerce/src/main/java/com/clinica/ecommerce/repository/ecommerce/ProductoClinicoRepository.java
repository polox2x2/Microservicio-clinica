package com.clinica.ecommerce.repository.ecommerce;

import com.clinica.ecommerce.model.ecommerce.ProductoClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio JPA de productos
public interface ProductoClinicoRepository extends JpaRepository<ProductoClinico, Long> {

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    List<ProductoClinico> findByActivoTrue();

    List<ProductoClinico> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);
}
