package com.clinica.ecommerce.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservice-citas", url = "${clients.citas.url:}")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioClientDto obtenerUsuarioPorId(@PathVariable("id") Long id);
}
