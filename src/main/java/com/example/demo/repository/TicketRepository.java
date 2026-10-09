package com.example.demo.repository;

import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByEventIdAndStatus(Long eventId, TicketStatus status);

    // Выполняет SELECT ... FOR UPDATE в PostgreSQL
    // Блокирует строку билета до завершения транзакции, предотвращая overbooking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdWithLock(@Param("id") Long id);
}