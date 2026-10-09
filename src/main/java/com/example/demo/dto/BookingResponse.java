package com.example.demo.dto;

import com.example.demo.entity.BookingStatus;
import java.time.OffsetDateTime;

public record BookingResponse(
        Long id,
        Long userId,
        Long ticketId,
        BookingStatus status,
        OffsetDateTime createdAt
) {}