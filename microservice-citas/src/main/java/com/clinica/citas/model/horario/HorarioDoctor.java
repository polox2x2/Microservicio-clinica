package com.clinica.citas.model.horario;

import com.clinica.citas.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_doctores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDoctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Usuario doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Builder.Default
    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos = 30;

    @Column(nullable = false, length = 50)
    private String consultorio;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @PrePersist
    void prePersist() {
        if (duracionMinutos == null) {
            duracionMinutos = 30;
        }
        if (activo == null) {
            activo = Boolean.TRUE;
        }
    }
}
