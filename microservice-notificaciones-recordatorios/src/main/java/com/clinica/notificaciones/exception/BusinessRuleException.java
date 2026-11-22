package com.clinica.notificaciones.exception;

// Excepción por regla de negocio
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
