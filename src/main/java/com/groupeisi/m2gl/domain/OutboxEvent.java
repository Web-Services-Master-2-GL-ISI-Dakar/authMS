package com.groupeisi.m2gl.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity for Outbox pattern - ensures reliable event publishing.
 * Events are first stored in this table within the same transaction as the business operation,
 * then asynchronously published to Kafka by the OutboxPoller.
 */
@Entity
@Table(name = "outbox_events", indexes = {
    @Index(name = "idx_outbox_unsent", columnList = "sent_at"),
    @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type, aggregate_id")
})
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    public OutboxEvent() {}

    private OutboxEvent(Builder builder) {
        this.id = builder.id;
        this.aggregateType = builder.aggregateType;
        this.aggregateId = builder.aggregateId;
        this.eventType = builder.eventType;
        this.payload = builder.payload;
        this.correlationId = builder.correlationId;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.sentAt = builder.sentAt;
        this.retryCount = builder.retryCount;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void markAsSent() {
        this.sentAt = Instant.now();
    }

    public boolean isSent() {
        return this.sentAt != null;
    }

    public static class Builder {
        private UUID id;
        private String aggregateType;
        private String aggregateId;
        private String eventType;
        private String payload;
        private String correlationId;
        private Instant createdAt;
        private Instant sentAt;
        private int retryCount = 0;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder aggregateType(String aggregateType) {
            this.aggregateType = aggregateType;
            return this;
        }

        public Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder sentAt(Instant sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public OutboxEvent build() {
            return new OutboxEvent(this);
        }
    }
}
