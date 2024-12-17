package com.itm.space.taskservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TaskException extends RuntimeException {

    private final HttpStatus httpStatus;

    public TaskException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}