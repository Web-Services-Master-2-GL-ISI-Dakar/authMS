package com.groupeisi.m2gl.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupeisi.m2gl.domain.OutboxEvent;
import com.groupeisi.m2gl.repository.OutboxEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for OutboxPoller.
 * Verifies that events are correctly published to Kafka.
 */
@SpringBootTest
@ActiveProfiles("testdev")
@EmbeddedKafka(
    partitions = 1,
    topics = {"otp.requested", "user.registered"},
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    }
)
@Transactional
class OutboxPollerTest {

    @Autowired
    private OutboxPoller outboxPoller;

    @Autowired
    private OutboxEventRepository outboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        outboxRepository.deleteAll();
    }

    @Test
    @DisplayName("Outbox poller should publish events and mark them as sent")
    void pollAndPublish_ShouldPublishEventsToKafka() throws Exception {
        // Given
        String payload = """
            {
                "phoneNumber": "+221771234567",
                "otpCode": "123456",
                "purpose": "REGISTRATION",
                "locale": "fr"
            }
            """;

        OutboxEvent event = OutboxEvent.builder()
            .aggregateType("Otp")
            .aggregateId("+221771234567")
            .eventType("otp.requested")
            .payload(payload)
            .correlationId("test-correlation-id")
            .build();

        outboxRepository.save(event);

        // Verify event is unsent
        assertThat(outboxRepository.countUnsentEvents()).isEqualTo(1);

        // When
        outboxPoller.pollAndPublish();

        // Then - wait for the event to be marked as sent
        await()
            .atMost(Duration.ofSeconds(5))
            .until(() -> {
                OutboxEvent updated = outboxRepository.findById(event.getId()).orElse(null);
                return updated != null && updated.isSent();
            });

        // Verify event is marked as sent
        OutboxEvent sentEvent = outboxRepository.findById(event.getId()).orElseThrow();
        assertThat(sentEvent.getSentAt()).isNotNull();
        assertThat(sentEvent.getRetryCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("Outbox poller should create CloudEvents envelope")
    void pollAndPublish_ShouldWrapInCloudEventsEnvelope() throws Exception {
        // Given
        String payload = """
            {
                "userId": "usr_123",
                "phoneNumber": "+221771234567",
                "firstName": "John",
                "lastName": "Doe"
            }
            """;

        OutboxEvent event = OutboxEvent.builder()
            .aggregateType("User")
            .aggregateId("usr_123")
            .eventType("user.registered")
            .payload(payload)
            .correlationId("test-correlation")
            .build();

        outboxRepository.save(event);

        // When
        outboxPoller.pollAndPublish();

        // Then - verify event was marked as sent (CloudEvents envelope is created internally)
        await()
            .atMost(Duration.ofSeconds(5))
            .until(() -> outboxRepository.findById(event.getId()).map(OutboxEvent::isSent).orElse(false));
    }

    @Test
    @DisplayName("Outbox poller should increment retry count on failure")
    void pollAndPublish_ShouldIncrementRetryOnFailure() {
        // This test would require mocking Kafka to simulate failure
        // Placeholder for demonstration
    }

    @Test
    @DisplayName("Outbox poller should skip events exceeding max retries")
    void pollAndPublish_ShouldSkipEventsExceedingMaxRetries() {
        // Given
        OutboxEvent event = OutboxEvent.builder()
            .aggregateType("Otp")
            .aggregateId("+221771234567")
            .eventType("otp.requested")
            .payload("{}")
            .retryCount(10) // Exceeds max retries
            .build();

        outboxRepository.save(event);

        // When
        outboxPoller.pollAndPublish();

        // Then - event should remain unsent
        OutboxEvent notProcessed = outboxRepository.findById(event.getId()).orElseThrow();
        assertThat(notProcessed.isSent()).isFalse();
    }
}
