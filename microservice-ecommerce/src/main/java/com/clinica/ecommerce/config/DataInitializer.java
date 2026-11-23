package com.clinica.ecommerce.config;

import com.clinica.ecommerce.model.ecommerce.DetalleOrden;
import com.clinica.ecommerce.model.ecommerce.EstadoOrden;
import com.clinica.ecommerce.model.ecommerce.OrdenCompra;
import com.clinica.ecommerce.model.ecommerce.ProductoClinico;
import com.clinica.ecommerce.repository.ecommerce.OrdenCompraRepository;
import com.clinica.ecommerce.repository.ecommerce.ProductoClinicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductoClinicoRepository productoClinicoRepository;
    private final OrdenCompraRepository ordenCompraRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (productoClinicoRepository.count() > 0) {
            log.debug("La base de datos clinica_ecommerce ya contiene datos. Se omite carga inicial.");
            return;
        }

        log.info("Inicializando datos de prueba en clinica_ecommerce...");

        ProductoClinico tensiometro = productoClinicoRepository.save(ProductoClinico.builder()
                .sku("EQP-TEN-001")
                .nombre("Tensiómetro Digital de Brazo Omron")
                .descripcion("Monitor automático de presión arterial con detector de arritmias y memoria para 60 mediciones")
                .categoria("Equipos Médicos")
                .precio(new BigDecimal("185.50"))
                .stock(25)
                .stockMinimo(5)
                .requiereReceta(false)
                .activo(true)
                .build());

        ProductoClinico oximetro = productoClinicoRepository.save(ProductoClinico.builder()
                .sku("EQP-OXI-002")
                .nombre("Oxímetro de Pulso Digital Fingertip")
                .descripcion("Medidor portátil de saturación de oxígeno (SpO2) y frecuencia cardíaca con pantalla OLED")
                .categoria("Equipos Médicos")
                .precio(new BigDecimal("65.00"))
                .stock(40)
                .stockMinimo(8)
                .requiereReceta(false)
                .activo(true)
                .build());

        ProductoClinico glucometro = productoClinicoRepository.save(ProductoClinico.builder()
                .sku("EQP-GLU-003")
                .nombre("Kit Glucómetro Accu-Chek Instant + 50 Tiras")
                .descripcion("Sistema de monitoreo de glucosa en sangre con lancetero y estuche clínico")
                .categoria("Diagnóstico")
                .precio(new BigDecimal("145.00"))
                .stock(18)
                .stockMinimo(5)
                .requiereReceta(false)
                .activo(true)
                .build());

        ProductoClinico losartan = productoClinicoRepository.save(ProductoClinico.builder()
                .sku("FAR-LOS-004")
                .nombre("Losartán Potásico 50mg (Caja x 30 comp.)")
                .descripcion("Antihipertensivo para control de presión arterial elevada")
                .categoria("Farmacia Cardiovascular")
                .precio(new BigDecimal("38.00"))
                .stock(60)
                .stockMinimo(10)
                .requiereReceta(true)
                .activo(true)
                .build());

        ProductoClinico fotoprotector = productoClinicoRepository.save(ProductoClinico.builder()
                .sku("DER-PRO-005")
                .nombre("Fotoprotector Dermatológico SPF 50+ Gel-Crema")
                .descripcion("Protector solar clínico hipoalergénico para piel sensible o con tendencia atópica")
                .categoria("Dermocosmética")
                .precio(new BigDecimal("92.00"))
                .stock(30)
                .stockMinimo(6)
                .requiereReceta(false)
                .activo(true)
                .build());

        ProductoClinico botiquin = productoClinicoRepository.save(ProductoClinico.builder()
                .sku("INS-BOT-006")
                .nombre("Botiquín Clínico de Primeros Auxilios Familiar")
                .descripcion("Incluye gasas estériles, vendas elásticas, antiséptico, termómetro digital y tijera quirúrgica")
                .categoria("Insumos Médicos")
                .precio(new BigDecimal("110.00"))
                .stock(15)
                .stockMinimo(5)
                .requiereReceta(false)
                .activo(true)
                .build());

        productoClinicoRepository.save(ProductoClinico.builder()
                .sku("PAQ-CHK-007")
                .nombre("Paquete Chequeo Preventivo Integral")
                .descripcion("Incluye hemograma completo, perfil lipídico, glucosa basal y consulta médica preventiva")
                .categoria("Paquetes Clínicos")
                .precio(new BigDecimal("290.00"))
                .stock(3)
                .stockMinimo(5)
                .requiereReceta(false)
                .activo(true)
                .build());

        OrdenCompra orden1 = OrdenCompra.builder()
                .codigoOrden("ORD-2026-0001")
                .clienteId(3L)
                .nombreCliente("Lucía Torres Vega")
                .emailCliente("ltorres@correo.com")
                .telefonoCliente("+51912345678")
                .estado(EstadoOrden.PENDIENTE)
                .total(new BigDecimal("436.00"))
                .build();
        orden1.agregarDetalle(crearDetalle(tensiometro, 2));
        orden1.agregarDetalle(crearDetalle(oximetro, 1));

        OrdenCompra orden2 = OrdenCompra.builder()
                .codigoOrden("ORD-2026-0002")
                .clienteId(7L)
                .nombreCliente("Jorge Ramírez Quispe")
                .emailCliente("jramirez@correo.com")
                .telefonoCliente("+51923456789")
                .estado(EstadoOrden.PAGADA)
                .total(new BigDecimal("221.00"))
                .build();
        orden2.agregarDetalle(crearDetalle(glucometro, 1));
        orden2.agregarDetalle(crearDetalle(losartan, 2));

        OrdenCompra orden3 = OrdenCompra.builder()
                .codigoOrden("ORD-2026-0003")
                .clienteId(8L)
                .nombreCliente("María Fernanda Salazar")
                .emailCliente("msalazar@correo.com")
                .telefonoCliente("+51934567890")
                .estado(EstadoOrden.ENTREGADA)
                .total(new BigDecimal("202.00"))
                .build();
        orden3.agregarDetalle(crearDetalle(fotoprotector, 1));
        orden3.agregarDetalle(crearDetalle(botiquin, 1));

        ordenCompraRepository.saveAll(List.of(orden1, orden2, orden3));

        log.info("Datos iniciales cargados en clinica_ecommerce: 7 productos clínicos y 3 órdenes de compra.");
    }

    private DetalleOrden crearDetalle(ProductoClinico producto, int cantidad) {
        return DetalleOrden.builder()
                .producto(producto)
                .cantidad(cantidad)
                .precioUnitario(producto.getPrecio())
                .subtotal(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)))
                .build();
    }
}
