package com.clinica.ecommerce.exception;

import java.time.LocalDateTime;
import java.util.Map;

// Estructura estándar de error REST
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
}
