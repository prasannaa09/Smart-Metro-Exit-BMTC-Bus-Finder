package com.example.bmrcl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for transit-related errors.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransitException extends RuntimeException {
    public TransitException(String message) {
        super(message);
    }

    public TransitException(String message, Throwable cause) {
        super(message, cause);
    }
}