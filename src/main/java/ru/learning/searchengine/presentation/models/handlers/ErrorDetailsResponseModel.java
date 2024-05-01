package ru.learning.searchengine.presentation.models.handlers;

import lombok.Data;
import org.springframework.web.method.HandlerMethod;

@Data
public class ErrorDetailsResponseModel {
    private String errorMessage;

    private String errorClassName;

    private String controllerClassName;

    private StackTraceElement[] stackTraceElements;

    public ErrorDetailsResponseModel(Throwable throwable) {
        this.errorMessage = throwable.getMessage();
        this.stackTraceElements = throwable.getStackTrace();
        this.errorClassName = throwable.getClass().getName();
    }

    public ErrorDetailsResponseModel(Throwable throwable, HandlerMethod handlerMethod) {
        this(throwable);
        this.controllerClassName = handlerMethod.getBeanType().getName();
    }
}
