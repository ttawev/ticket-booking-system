package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Ticket ID is required")
        Long ticketId
) {}