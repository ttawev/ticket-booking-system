package com.example.demo.service;

import com.example.demo.dto.CreateBookingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrentBookingTest {

    @Autowired
    private BookingService bookingService;

    @Test
    void testConcurrentBookingsForSameTicket() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Long ticketId = 1L;

        for (int i = 0; i < numberOfThreads; i++) {
            long userId = (i % 2 == 0) ? 1L : 2L;
            executorService.submit(() -> {
                try {
                    latch.await();
                    bookingService.createBooking(new CreateBookingRequest(userId, ticketId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });
        }

        latch.countDown();
        executorService.shutdown();

        while (!executorService.isTerminated()) {
            Thread.sleep(50);
        }

        assertEquals(1, successCount.get(), "Должно быть ровно 1 успешное бронирование");
        assertEquals(99, failCount.get(), "Остальные 99 запросов должны завершиться ошибкой");
    }
}