package com.clinica.citas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Cliente Feign hacia notificaciones
@FeignClient(name = "microservice-notificaciones-recordatorios", url = "${clients.notificaciones.url:}")
public interface NotificacionClient {

    @PostMapping("/api/notificaciones")
    void enviarNotificacion(@RequestBody NotificacionClientRequestDto request);
}
