package com.groupeisi.m2gl.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupeisi.m2gl.domain.OutboxEvent;
import com.groupeisi.m2gl.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Outbox Poller - polls the outbox table and publishes events to Kafka.
 * Implements the Outbox pattern for reliable event publishing.
 * 
 * Events are wrapped in CloudEvents envelope before publishing.
 */
@Component
public class OutboxPoller {

    private static final Logger LOG = LoggerFactory.getLogger(OutboxPoller.class);
    private static final int BATCH_SIZE = 100;
    private static final int MAX_RETRIES = 5;
    private static final String SOURCE = "ond-money/auth-service";
    private static final String SPEC_VERSION = "1.0";

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPoller(OutboxEventRepository outboxRepository,
                        KafkaTemplate<String, String> kafkaTemplate,
                        ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Polls the outbox table every 100ms and publishes pending events.
     */
    @Scheduled(fixedDelay = 100)
    @Transactional
    public void pollAndPublish() {
        List<OutboxEvent> events = outboxRepository.findUnsentEventsWithRetryLimit(MAX_RETRIES, BATCH_SIZE);
        
        if (events.isEmpty()) {
            return;
        }

        LOG.debug("Found {} unsent events in outbox", events.size());

        for (OutboxEvent event : events) {
            try {
                publishEvent(event);
                event.markAsSent();
                outboxRepository.save(event);
                LOG.info("Successfully published event {} to topic {}", event.getId(), event.getEventType());
            } catch (Exception e) {
                LOG.error("Failed to publish event {}: {}", event.getId(), e.getMessage());
                event.incrementRetryCount();
                outboxRepository.save(event);
                
                if (event.getRetryCount() >= MAX_RETRIES) {
                    LOG.error("Event {} exceeded max retries, will not retry", event.getId());
                }
            }
        }
    }

    /**
     * Publishes a single event to Kafka wrapped in CloudEvents envelope.
     */
    private void publishEvent(OutboxEvent event) throws Exception {
        String cloudEvent = wrapInCloudEvent(event);
        
        kafkaTemplate.send(
            event.getEventType(),      // Topic name (e.g., "user.registered")
            event.getAggregateId(),    // Key for partitioning
            cloudEvent                  // CloudEvents envelope
        ).get(5, TimeUnit.SECONDS);    // Wait for acknowledgment
    }

    /**
     * Wraps the event payload in a CloudEvents envelope.
     */
    private String wrapInCloudEvent(OutboxEvent event) throws Exception {
        String eventId = "evt_" + UUID.randomUUID().toString();
        
        // Parse the original payload
        Object data = objectMapper.readValue(event.getPayload(), Object.class);
        
        // Build CloudEvents envelope
        Map<String, Object> cloudEvent = Map.of(
            "specversion", SPEC_VERSION,
            "id", eventId,
            "source", SOURCE,
            "type", event.getEventType(),
            "datacontenttype", "application/json",
            "time", Instant.now().toString(),
            "subject", event.getAggregateId(),
            "data", data,
            "ondmoney", Map.of(
                "correlationId", event.getCorrelationId() != null ? event.getCorrelationId() : eventId,
                "version", "1.0.0",
                "environment", getEnvironment()
            )
        );
        
        return objectMapper.writeValueAsString(cloudEvent);
    }

    /**
     * Gets the current environment from system properties or defaults to development.
     */
    private String getEnvironment() {
        String profile = System.getProperty("spring.profiles.active", "dev");
        return switch (profile) {
            case "prod" -> "production";
            case "staging" -> "staging";
            default -> "development";
        };
    }

    /**
     * Cleanup old sent events (runs daily).
     */
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM daily
    @Transactional
    public void cleanupOldEvents() {
        Instant thirtyDaysAgo = Instant.now().minusSeconds(30 * 24 * 60 * 60);
        int deleted = outboxRepository.deleteOldSentEvents(thirtyDaysAgo);
        if (deleted > 0) {
            LOG.info("Cleaned up {} old outbox events", deleted);
        }
    }
}
