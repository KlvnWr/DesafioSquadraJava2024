package com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionHandlerTotal {
//    @ExceptionHandler(ListarException.class)
//    public ResponseEntity<String> handleListarException(ListarException ex) {
//        return ResponseEntity.status(ex.getStatus())
//                .body(ex.toJson());
//    }

    @ExceptionHandler(ListarException.class)
    public ResponseEntity<String> handleListarException(ListarException ex, WebRequest request) {
        String jsonResponse = ex.toJson();
        return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
        String errorMessage = "O campo deve ser um número válido.";
        ListarException customException = new ListarException(errorMessage, HttpStatus.BAD_REQUEST);
        String jsonResponse = customException.toJson();
        return new ResponseEntity<>(jsonResponse, HttpStatus.BAD_REQUEST);
    }
}
