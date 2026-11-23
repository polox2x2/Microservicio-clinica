package com.clinica.citas.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionClientRequestDto {

    private Long destinatarioId;
    private String destinatarioNombre;
    private String destinatarioContacto;
    private String canal;
    private String tipoEvento;
    private Long referenciaId;
    private String asunto;
    private String mensaje;
    private Boolean enviarInmediatamente;
}
