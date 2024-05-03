package ru.learning.searchengine.infrastructure.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import ru.learning.searchengine.domain.dto.ErrorDetailsDto;
import ru.learning.searchengine.infrastructure.exceptions.SiteNotFoundException;
import ru.learning.searchengine.infrastructure.mappers.ErrorDetailsMapper;
import ru.learning.searchengine.presentation.models.StatusResponseModel;

@Component
@ControllerAdvice
@Slf4j
public class ControllersExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<StatusResponseModel> validationException(BindException e, HandlerMethod handlerMethod) {
        log.atError()
                .setCause(e)
                .addKeyValue("@invalidModel", e.getTarget())
                .log("Ошибка валидации значений во время обращения к контроллеру");
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(this.getErrorDetailsModel(e, httpStatus, handlerMethod), httpStatus);
    }

    @ExceptionHandler(SiteNotFoundException.class)
    public ResponseEntity<StatusResponseModel> siteNotFoundException(SiteNotFoundException e, HandlerMethod handlerMethod) {
        log.atError()
                .setCause(e)
                .log("Список сайтов для индексации пуст");
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(this.getErrorDetailsModel(e, httpStatus, handlerMethod), httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StatusResponseModel> exception(Exception e, HandlerMethod handlerMethod) {
        log.atError()
                .setCause(e)
                .log("Неизвестная ошибка во время обращения к контроллеру");
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(this.getErrorDetailsModel(e, httpStatus, handlerMethod), httpStatus);
    }

    private StatusResponseModel getErrorDetailsModel(Throwable throwable, HttpStatus status, HandlerMethod handlerMethod) {
        ErrorDetailsDto errorDetailsDto = new ErrorDetailsDto(throwable, status, handlerMethod);
        return ErrorDetailsMapper.INSTANCE.dtoToResponseModel(errorDetailsDto);
    }
}
