package com.clinica.citas.model.especialidad;

import jakarta.persistence.*;
import lombok.*;

// Entidad de especialidad médica
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

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @PrePersist
    void prePersist() {
        if (activo == null) {
            activo = Boolean.TRUE;
        }
    }
}
