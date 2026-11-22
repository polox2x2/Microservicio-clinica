package com.clinica.notificaciones.exception;

// Excepción por recurso no encontrado
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
