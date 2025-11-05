package com.xyz.carrentalservice.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LicenseValidationException.class)
    public ResponseEntity<Map<String,String>> handleLicense(LicenseValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RateUnavailableException.class)
    public ResponseEntity<Map<String,String>> handleRate(RateUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Car pricing service currently unavailable. Please try again later."));
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleNotFound(BookingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<Map<String,String>> handleBookingValidation(BookingValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<Map<String,String>> handleExternal(ExternalServiceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "External dependency failed. Please try again later."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleIllegal(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    // Handle validation annotations (@NotNull, @NotBlank, @Min, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .reduce((a,b) -> a + ", " + b)
                .orElse("Invalid input");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    // Handle enum deserialization and malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Invalid input";

        if (ex.getCause() instanceof InvalidFormatException invalidFormat) {
            if (invalidFormat.getTargetType().isEnum()) {
                message = String.format(
                        "Invalid value '%s' for field '%s'. Acceptable values: %s",
                        invalidFormat.getValue(),
                        invalidFormat.getPath().get(0).getFieldName(),
                        java.util.Arrays.toString(invalidFormat.getTargetType().getEnumConstants())
                );
            } else {
                message = invalidFormat.getOriginalMessage();
            }
        } else {
            ex.getMostSpecificCause();
            message = ex.getMostSpecificCause().getMessage();
        }

        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleAll(Exception ex) {
        // Log ex for ops (not returned to client)
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error. Please contact support."));
    }
}
