package com.itm.space.taskservice.controller.advice;

import com.itm.space.itmplatformcommonmodels.response.HttpErrorResponse;
import com.itm.space.taskservice.exception.TaskException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<HttpErrorResponse> handleServiceException(TaskException ex) {

        HttpStatus status = ex.getHttpStatus();
        HttpErrorResponse error = new HttpErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
                    MethodArgumentTypeMismatchException.class,
                    IllegalArgumentException.class,
                    ConstraintViolationException.class,
                    HttpMessageNotReadableException.class,
                    MissingServletRequestParameterException.class,
                    ConstraintViolationException.class,
                    HttpMessageNotReadableException.class
            })

    public ResponseEntity<HttpErrorResponse> handleInvalidArgument() {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpErrorResponse error = new HttpErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Неправильные аргументы запроса"
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpErrorResponse> handleForbidden() {

        HttpStatus status = HttpStatus.FORBIDDEN;
        HttpErrorResponse error = new HttpErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Недостаточно прав"
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> handleException() {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpErrorResponse error = new HttpErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                "Внутренняя ошибка сервера"
        );
        return new ResponseEntity<>(error, status);
    }
}