package com.civia.reportservice.service;

import com.civia.reportservice.model.entity.OutboxEvent;

/**
 * Interface for publishing outbox events to a message broker (e.g., Kafka / RabbitMQ).
 * To be implemented when the broker infrastructure is integrated.
 */
public interface EventPublisher {
    void publish(OutboxEvent event);
}
