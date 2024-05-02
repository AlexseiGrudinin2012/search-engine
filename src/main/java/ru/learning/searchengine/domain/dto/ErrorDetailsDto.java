package ru.learning.searchengine.domain.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

@Data
public class ErrorDetailsDto {
    private String errorMessage;

    private String errorClassName;

    private HttpStatus httpCode;

    private String controllerClassName;

    private StackTraceElement[] stackTraceElements;

    public ErrorDetailsDto(Throwable throwable, HttpStatus httpCode) {
        this.errorMessage = throwable.getMessage();
        this.stackTraceElements = throwable.getStackTrace();
        this.errorClassName = throwable.getClass().getName();
        this.httpCode = httpCode;
    }

    public ErrorDetailsDto(Throwable throwable, HttpStatus httpCode, HandlerMethod handlerMethod) {
        this(throwable, httpCode);
        this.controllerClassName = handlerMethod.getBeanType().getName();
    }
}
