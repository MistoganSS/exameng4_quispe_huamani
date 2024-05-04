package com.codigo.msquispehuamani.application.ExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex){
        logger.error("Ocurrió una exception inesperada: "+ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ha ocurrido un error, estamos trabajando para resolverlo.");
    }
}
