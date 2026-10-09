package com.example.demo.service;

import com.example.demo.dto.BookingResponse;
import com.example.demo.dto.CreateBookingRequest;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketStatus;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.TicketAlreadyBookedException;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.userId()));

        Ticket ticket = ticketRepository.findByIdWithLock(request.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + request.ticketId()));

        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new TicketAlreadyBookedException("Ticket " + request.ticketId() + " is already booked or pending payment");
        }

        ticket.setStatus(TicketStatus.PENDING_PAYMENT);
        ticketRepository.save(ticket);

        Booking booking = new Booking(user, ticket, BookingStatus.CREATED);

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getUser().getId(),
                savedBooking.getTicket().getId(),
                savedBooking.getStatus(),
                savedBooking.getCreatedAt()
        );
    }
}