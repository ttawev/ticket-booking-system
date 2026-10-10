# High-Concurrency Ticket Booking System

Небольшой REST API на Spring Boot для бронирования билетов. Основная задача проекта — решить проблему overbooking (двойного бронирования одного и того же билета) при одновременно поступающих запросах.

## Стек технологий

* Java 21
* Spring Boot 3 (Data JPA, Web)
* PostgreSQL 16
* Docker / Docker Compose
* JUnit 5, Spring Boot Test

## Как это работает

Для предотвращения состояния гонки (race condition) при покупке билета используется пессимистическая блокировка (`PESSIMISTIC_WRITE`). 

При поиске билета для бронирования выполняется SQL-запрос `SELECT ... FOR UPDATE`. PostgreSQL блокирует строчку с билетом до завершения транзакции. Если в этот момент приходят другие запросы на этот же билет, они ждут освобождения блокировки, после чего получают обновленный статус билета (`BOOKED`) и получают ошибку бронирования.

## REST API

### Бронирование билета

* **POST** `/api/v1/bookings`
* **Content-Type**: `application/json`

**Тело запроса:**
```json
{
  "userId": 1,
  "ticketId": 1
}

```
## Архитектурная схема

```mermaid
graph TD
    Client[Client / Postman] -->|POST /api/v1/bookings| Controller[BookingController]
    Controller --> Service[BookingService]
    Service -->|1. SELECT ... FOR UPDATE| Repo[TicketRepository]
    Repo -->|2. Lock Row| DB[(PostgreSQL)]
    Service -->|3. Check & Update Status| DB
    Service -->|4. Save Booking| DB

