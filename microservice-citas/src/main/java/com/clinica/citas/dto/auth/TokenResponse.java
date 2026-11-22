package com.clinica.citas.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Respuesta con el token JWT
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {

    private String token;
    private String tokenType;
    private long expiresInMs;
    private Long usuarioId;
    private String email;
    private String rol;
}
