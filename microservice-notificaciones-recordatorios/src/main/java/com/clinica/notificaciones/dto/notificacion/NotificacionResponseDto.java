package com.clinica.notificaciones.dto.notificacion;

import com.clinica.notificaciones.model.notificacion.CanalNotificacion;
import com.clinica.notificaciones.model.notificacion.EstadoNotificacion;
import com.clinica.notificaciones.model.notificacion.TipoEventoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Datos de salida de una notificación
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDto {

    private Long id;
    private Long usuarioId;
    private String destinatarioNombre;
    private String destinatarioContacto;
    private CanalNotificacion canal;
    private TipoEventoNotificacion tipoEvento;
    private Long referenciaId;
    private String asunto;
    private String mensaje;
    private EstadoNotificacion estado;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEnvio;
    private Integer intentos;
    private String detalleError;
    private LocalDateTime fechaCreacion;
}
