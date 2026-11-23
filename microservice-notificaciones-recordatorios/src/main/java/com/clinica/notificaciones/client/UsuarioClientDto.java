package com.clinica.notificaciones.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioClientDto {

    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    private String rol;
    private Boolean activo;
}
