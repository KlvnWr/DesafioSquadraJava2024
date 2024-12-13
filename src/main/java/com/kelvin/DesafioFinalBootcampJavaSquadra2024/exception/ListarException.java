package com.kelvin.DesafioFinalBootcampJavaSquadra2024.exception;

import org.springframework.http.HttpStatus;

public class ListarException extends Exception {
    private final HttpStatus status;

    public ListarException(String mensagem, HttpStatus status) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String toJson() {
        return String.format("{\n \"mensagem\": \"%s\",\n \"status\": %d\n}", getMessage(), status.value());
    }
}