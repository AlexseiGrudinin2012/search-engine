package ru.learning.searchengine.infrastructure.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import ru.learning.searchengine.domain.exceptions.StatisticsNotFoundException;
import ru.learning.searchengine.presentation.models.handlers.ErrorDetailsResponseModel;

@Component
@ControllerAdvice
public class ControllersExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ControllersExceptionHandler.class);

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorDetailsResponseModel> validationException(BindException e, HandlerMethod handlerMethod) {
        logger.atError()
                .setCause(e)
                .addKeyValue("@invalidModel", e.getTarget())
                .log("Ошибка валидации значений во время обращения к контроллеру");
        return new ResponseEntity<>(new ErrorDetailsResponseModel(e, handlerMethod), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StatisticsNotFoundException.class)
    public ResponseEntity<ErrorDetailsResponseModel> statisticsNotFoundException(StatisticsNotFoundException e, HandlerMethod handlerMethod) {
        logger.atError()
                .setCause(e)
                .log("Ошибка валидации значений во время обращения к контроллеру");
        return new ResponseEntity<>(new ErrorDetailsResponseModel(e, handlerMethod), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailsResponseModel> exception(Exception e, HandlerMethod handlerMethod) {
        logger.atError()
                .setCause(e)
                .log("Неизвестная ошибка во время обращения к контроллеру");
        return new ResponseEntity<>(new ErrorDetailsResponseModel(e, handlerMethod), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
