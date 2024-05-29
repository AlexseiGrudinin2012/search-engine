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

    private Throwable throwable;

    private StackTraceElement[] stackTraceElements;

    public ErrorDetailsDto(Throwable throwable) {
        this(throwable, null);
    }

    public ErrorDetailsDto(Throwable throwable, HttpStatus httpCode) {
        this.throwable = throwable;
        this.errorMessage = this.throwable.getMessage();
        this.stackTraceElements = this.throwable.getStackTrace();
        this.errorClassName = this.throwable.getClass().getName();
        this.httpCode = httpCode;
    }

    public ErrorDetailsDto(Throwable throwable, HttpStatus httpCode, HandlerMethod handlerMethod) {
        this(throwable, httpCode);
        this.controllerClassName = handlerMethod.getBeanType().getName();
    }
}
