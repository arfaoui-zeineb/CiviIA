package com.civia.reportservice.service.impl;

import com.civia.reportservice.model.entity.OutboxEvent;
import com.civia.reportservice.repository.OutboxEventRepository;
import com.civia.reportservice.service.OutboxEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public OutboxEvent recordEvent(Long aggregateId, String eventType, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            OutboxEvent outboxEvent = new OutboxEvent(aggregateId, eventType, jsonPayload);
            OutboxEvent saved = outboxEventRepository.save(outboxEvent);
            log.info("Recorded Outbox event [type={}, id={}, aggregateId={}]", eventType, saved.getEventId(), aggregateId);
            return saved;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox event payload for aggregateId={}", aggregateId, e);
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }
}
