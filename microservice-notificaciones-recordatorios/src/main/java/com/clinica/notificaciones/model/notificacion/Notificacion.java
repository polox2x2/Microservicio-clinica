package com.clinica.notificaciones.model.notificacion;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destinatario_id")
    private Long destinatarioId;

    @Column(name = "destinatario_nombre", nullable = false, length = 200)
    private String destinatarioNombre;

    @Column(name = "destinatario_contacto", nullable = false, length = 150)
    private String destinatarioContacto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CanalNotificacion canal;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 30)
    private TipoEventoNotificacion tipoEvento;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(nullable = false, length = 150)
    private String asunto;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Builder.Default
    @Column(nullable = false)
    private Integer intentos = 0;

    @Column(name = "detalle_error", length = 500)
    private String detalleError;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    void prePersist() {
        if (estado == null) {
            estado = EstadoNotificacion.PENDIENTE;
        }
        if (intentos == null) {
            intentos = 0;
        }
        if (fechaProgramada == null) {
            fechaProgramada = LocalDateTime.now();
        }
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}
