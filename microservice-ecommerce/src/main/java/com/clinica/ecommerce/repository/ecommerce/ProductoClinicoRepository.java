package com.clinica.ecommerce.repository.ecommerce;

import com.clinica.ecommerce.model.ecommerce.ProductoClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoClinicoRepository extends JpaRepository<ProductoClinico, Long> {

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    List<ProductoClinico> findByActivoTrue();

    List<ProductoClinico> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);

    @Query("SELECT p FROM ProductoClinico p WHERE p.activo = true AND p.stock <= p.stockMinimo ORDER BY p.stock ASC")
    List<ProductoClinico> findProductosConStockBajo();
}
