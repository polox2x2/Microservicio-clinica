package com.clinica.citas.model.especialidad;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "especialidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(name = "tarifa_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaBase;

    @Builder.Default
    @Column(name = "duracion_estandar_minutos", nullable = false)
    private Integer duracionEstandarMinutos = 30;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @PrePersist
    void prePersist() {
        if (duracionEstandarMinutos == null) {
            duracionEstandarMinutos = 30;
        }
        if (activo == null) {
            activo = Boolean.TRUE;
        }
    }
}
