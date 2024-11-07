package org.example.weathersensor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleIllegalArgument() {
        var exception = new IllegalArgumentException("Invalid argument");
        var response = handler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody());
    }

    @Test
    void shouldHandleIllegalArgumentWithEmptyMessage() {
        var exception = new IllegalArgumentException("");
        var response = handler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("", response.getBody());
    }

    @Test
    void shouldHandleGeneralException() {
        var exception = new Exception("Generic exception");
        var response = handler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An exception occurred: Generic exception", response.getBody());
    }

    @Test
    void shouldHandleGeneralExceptionWithNullMessage() {
        var exception = new Exception((String) null);
        var response = handler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An exception occurred: null", response.getBody());
    }
}