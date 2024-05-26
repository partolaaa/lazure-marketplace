package com.lazure.partola.exception;

/**
 * @author Ivan Partola
 */
public class TransactionNotAddedException extends RuntimeException{
    public TransactionNotAddedException (String message) {
        super(message);
    }
}
