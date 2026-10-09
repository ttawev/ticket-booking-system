package com.example.demo.config;

import com.example.demo.entity.Event;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.TicketRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public DataInitializer(UserRepository userRepository,
                           EventRepository eventRepository,
                           TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User user1 = new User();
            user1.setEmail("testuser1@example.com");
            user1.setName("Test User 1");
            userRepository.save(user1);

            User user2 = new User();
            user2.setEmail("testuser2@example.com");
            user2.setName("Test User 2");
            userRepository.save(user2);

            Event event = new Event();
            event.setTitle("Rock Concert");
            event.setStartTime(OffsetDateTime.now().plusDays(7));
            eventRepository.save(event);

            Ticket ticket = new Ticket();
            ticket.setEvent(event);
            ticket.setSeatNumber("A1");
            ticket.setPrice(new BigDecimal("100.00"));
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setVersion(0L);
            ticketRepository.save(ticket);
        }
    }
}