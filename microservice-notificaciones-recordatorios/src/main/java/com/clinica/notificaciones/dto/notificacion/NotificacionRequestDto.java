package com.clinica.notificaciones.dto.notificacion;

import com.clinica.notificaciones.model.notificacion.CanalNotificacion;
import com.clinica.notificaciones.model.notificacion.TipoEventoNotificacion;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionRequestDto {

    @JsonAlias("usuarioId")
    private Long destinatarioId;

    @Size(max = 200, message = "El nombre del destinatario no debe exceder 200 caracteres")
    private String destinatarioNombre;

    @Size(max = 150, message = "El contacto del destinatario no debe exceder 150 caracteres")
    private String destinatarioContacto;

    @NotNull(message = "El canal de notificación es obligatorio")
    private CanalNotificacion canal;

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEventoNotificacion tipoEvento;

    private Long referenciaId;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 150, message = "El asunto no debe exceder 150 caracteres")
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no debe exceder 1000 caracteres")
    private String mensaje;

    private LocalDateTime fechaProgramada;

    private Boolean enviarInmediatamente;
}
