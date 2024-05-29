package com.lazure.partola.exception;

/**
 * @author Ivan Partola
 */
public class LoginFailedException extends RuntimeException {
    public LoginFailedException(String message) {
        super(message);
    }
}