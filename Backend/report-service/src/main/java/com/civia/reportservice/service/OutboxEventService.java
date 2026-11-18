package com.civia.reportservice.service;

import com.civia.reportservice.model.entity.OutboxEvent;

public interface OutboxEventService {
    OutboxEvent recordEvent(Long aggregateId, String eventType, Object payload);
}
