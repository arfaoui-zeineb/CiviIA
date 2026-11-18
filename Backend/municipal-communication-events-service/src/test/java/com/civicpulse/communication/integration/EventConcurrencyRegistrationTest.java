package com.civicpulse.communication.integration;

import com.civicpulse.communication.domain.entity.Category;
import com.civicpulse.communication.domain.entity.Event;
import com.civicpulse.communication.domain.enums.EventStatus;
import com.civicpulse.communication.domain.enums.RegistrationStatus;
import com.civicpulse.communication.repository.CategoryRepository;
import com.civicpulse.communication.repository.EventRegistrationRepository;
import com.civicpulse.communication.repository.EventRepository;
import com.civicpulse.communication.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EventConcurrencyRegistrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EventRegistrationRepository registrationRepository;

    private Long eventId;
    private final int CAPACITY = 5;
    private final int CONCURRENT_ATTEMPTS = 20;

    @BeforeEach
    void setUp() {
        registrationRepository.deleteAll();
        eventRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = categoryRepository.save(Category.builder()
                .name("Sport")
                .description("Activités sportives")
                .active(true)
                .build());

        Event event = Event.builder()
                .category(category)
                .departmentId(1L)
                .title("Tournoi de Futsal")
                .description("Tournoi municipal inter-quartiers")
                .startDate(OffsetDateTime.now().plusDays(2))
                .endDate(OffsetDateTime.now().plusDays(3))
                .capacity(CAPACITY)
                .registeredCount(0)
                .status(EventStatus.OPEN)
                .build();

        event = eventRepository.save(event);
        eventId = event.getId();
    }

    @Test
    @DisplayName("Concurrence: Exactement CAPACITY inscriptions réussies parmi 20 tentatives simultanées")
    void testConcurrentRegistrationsPreventOverbooking() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_ATTEMPTS);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(CONCURRENT_ATTEMPTS);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 1; i <= CONCURRENT_ATTEMPTS; i++) {
            final long userId = (long) i;
            executor.submit(() -> {
                try {
                    startSignal.await(); // Synchronize thread start
                    eventService.registerUser(eventId, userId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        // Fire all threads at once
        startSignal.countDown();
        doneSignal.await();
        executor.shutdown();

        // Verification
        Event updatedEvent = eventRepository.findById(eventId).orElseThrow();

        assertThat(successCount.get()).isEqualTo(CAPACITY);
        assertThat(failureCount.get()).isEqualTo(CONCURRENT_ATTEMPTS - CAPACITY);
        assertThat(updatedEvent.getRegisteredCount()).isEqualTo(CAPACITY);
        assertThat(updatedEvent.getStatus()).isEqualTo(EventStatus.FULL);

        long activeRegistrations = registrationRepository.findAll().stream()
                .filter(r -> r.getStatus() == RegistrationStatus.REGISTERED)
                .count();
        assertThat(activeRegistrations).isEqualTo(CAPACITY);
    }
}

