package com.nure.lazure.partola.advicers;

import com.nure.lazure.partola.exceptions.DataNotRetrievedException;
import com.nure.lazure.partola.exceptions.ProductNotAddedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Ivan Partola
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataNotRetrievedException.class)
    public ResponseEntity<String> handleDataNotRetrievedException(DataNotRetrievedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    @ExceptionHandler(ProductNotAddedException.class)
    public ResponseEntity<String> handleProductNotAddedException(ProductNotAddedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
