package com.clinica.citas.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para enviar notificaciones remotas
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionClientRequestDto {

    private Long usuarioId;
    private String destinatarioNombre;
    private String destinatarioContacto;
    private String canal;
    private String tipoEvento;
    private Long referenciaId;
    private String asunto;
    private String mensaje;
    private Boolean enviarInmediatamente;
}
