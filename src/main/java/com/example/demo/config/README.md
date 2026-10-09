# High-Concurrency Ticket Booking System

Высоконагруженный сервисный REST API для бронирования билетов, спроектированный для обработки параллельных запросов в условиях высокой нагрузки с гарантией защиты от overbooking (состояния гонки / race condition).

---

## 🛠 Технологический стек

* **Язык программирования**: Java 21 (Records, pattern matching, Virtual Threads ready)
* **Фреймворк**: Spring Boot 3.x (Spring Data JPA, Spring Web)
* **База данных**: PostgreSQL 16
* **Контейнеризация**: Docker / Docker Compose
* **Тестирование**: JUnit 5, Spring Boot Test, Java Concurrency Utilities (`ExecutorService`, `CountDownLatch`, `AtomicInteger`)
* **Сборка**: Apache Maven

---

## 🎯 Архитектурные решения и ключевые фичи

### 1. Решение проблемы Overbooking (Race Condition)
При высокой параллельной нагрузке (например, когда 100 пользователей одновременно пытаются купить 1 последний билет) возникают риски двойной продажи (lost updates / race conditions).

В проекте применен подход **Pessimistic Locking (`PESSIMISTIC_WRITE`)**:
* При вызове метода поиска билета выполнение транзакции генерирует SQL-запрос `SELECT ... FOR UPDATE`.
* PostgreSQL блокирует выбранную строку билета на уровне СУБД.
* Другие параллельные транзакции ждут завершения первой транзакции. Первая транзакция меняет статус билета на `BOOKED`, а остальные при чтении получают обновлённое состояние и отклоняют бронирование.

### 2. Изоляция и управление транзакциями
Все сервисные операции обёрнуты в `@Transactional`. Если во время записи или валидации пользователя происходит сбой, СУБД делает откат (rollback), обеспечивая атомарность (ACID).

### 3. Автоматическая инициализация данных
В проект встроен `DataInitializer` (`CommandLineRunner`), который подготавливает стартовые данные (`User`, `Event`, `Ticket`) при старте контейнера, что позволяет тестировать и запускать проект «из коробки».

---

## 📊 REST API Endpoints

### Бронирование билета
* **URL**: `POST /api/v1/bookings`
* **Content-Type**: `application/json`

**Запрос (Request Body):**
```json
{
  "userId": 1,
  "ticketId": 1
}