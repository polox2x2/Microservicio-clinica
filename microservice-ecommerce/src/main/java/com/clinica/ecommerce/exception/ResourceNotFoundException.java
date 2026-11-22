package com.clinica.ecommerce.exception;

// Excepción por recurso no encontrado
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
