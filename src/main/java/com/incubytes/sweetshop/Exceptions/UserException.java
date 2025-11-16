package com.incubytes.sweetshop.Exceptions;

import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;

@Getter
@Setter
public class UserException extends RuntimeException {
    private final HttpStatus status;

    public UserException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public UserException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
