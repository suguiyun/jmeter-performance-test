package com.cryptoex.errors;

public class BadRequestException extends RuntimeException {
    public final String error;
    public final String data;


    public BadRequestException(String error, String data, String message) {
        super(message);
        this.error = error;
        this.data = data;
    }

    public static class ApiErrorResponse {
        public String error;
        public String data;
        public String message;

        public ApiErrorResponse() {
        }
    }
}