package com.groupeisi.m2gl.repository;

import com.groupeisi.m2gl.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for OutboxEvent entity.
 * Provides methods to manage outbox events for reliable Kafka publishing.
 */
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * Finds unsent events ordered by creation time.
     * Used by the OutboxPoller to publish pending events.
     *
     * @param limit maximum number of events to fetch
     * @return list of unsent events
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.sentAt IS NULL ORDER BY e.createdAt ASC LIMIT :limit")
    List<OutboxEvent> findUnsentEvents(@Param("limit") int limit);

    /**
     * Finds unsent events with retry count below threshold.
     * Allows filtering out events that have failed too many times.
     *
     * @param maxRetries maximum retry count threshold
     * @param limit maximum number of events to fetch
     * @return list of unsent events eligible for retry
     */
    @Query("SELECT e FROM OutboxEvent e WHERE e.sentAt IS NULL AND e.retryCount < :maxRetries ORDER BY e.createdAt ASC LIMIT :limit")
    List<OutboxEvent> findUnsentEventsWithRetryLimit(@Param("maxRetries") int maxRetries, @Param("limit") int limit);

    /**
     * Deletes events that were successfully sent before the specified time.
     * Used for cleanup of old processed events.
     *
     * @param before delete events sent before this time
     * @return number of deleted events
     */
    @Query("DELETE FROM OutboxEvent e WHERE e.sentAt IS NOT NULL AND e.sentAt < :before")
    int deleteOldSentEvents(@Param("before") Instant before);

    /**
     * Counts unsent events.
     * Useful for monitoring backlog.
     *
     * @return count of unsent events
     */
    @Query("SELECT COUNT(e) FROM OutboxEvent e WHERE e.sentAt IS NULL")
    long countUnsentEvents();

    /**
     * Finds events by aggregate type and ID.
     * Useful for debugging and auditing.
     *
     * @param aggregateType the aggregate type
     * @param aggregateId the aggregate ID
     * @return list of events for the specified aggregate
     */
    List<OutboxEvent> findByAggregateTypeAndAggregateId(String aggregateType, String aggregateId);
}
