package com.example.demo.exception;

public class TicketAlreadyBookedException extends RuntimeException {
    public TicketAlreadyBookedException(String message) {
        super(message);
    }
}