package com.groupeisi.m2gl.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupeisi.m2gl.domain.OutboxEvent;
import com.groupeisi.m2gl.domain.UtilisateurAuth;
import com.groupeisi.m2gl.event.payload.OtpRequestedEvent;
import com.groupeisi.m2gl.event.payload.OtpRequestedEvent.OtpPurpose;
import com.groupeisi.m2gl.event.payload.UserRegisteredEvent;
import com.groupeisi.m2gl.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service for publishing authentication-related events.
 * Uses the Outbox pattern for reliable delivery - events are first stored
 * in the outbox table within the same transaction, then asynchronously
 * published to Kafka.
 */
@Service
public class AuthEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(AuthEventProducer.class);
    private static final String USER_REGISTERED_TOPIC = "user.registered";
    private static final String OTP_REQUESTED_TOPIC = "otp.requested";
    private static final int OTP_EXPIRY_MINUTES = 5;

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public AuthEventProducer(OutboxEventRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Publishes a user.registered event when a new user completes registration.
     * This triggers wallet creation in TxEngineMS.
     *
     * @param user the registered user
     * @param correlationId the correlation ID for request tracing
     */
    @Transactional
    public void publishUserRegistered(UtilisateurAuth user, String correlationId) {
        LOG.info("Publishing user.registered event for user: {}", user.getId());

        UserRegisteredEvent event = UserRegisteredEvent.builder()
            .userId("usr_" + user.getId())
            .keycloakId(user.getKeycloakId())
            .phoneNumber(user.getNumeroTelephone())
            .firstName(user.getPrenom())
            .lastName(user.getNom())
            .email(user.getEmail())
            .registeredAt(user.getDateCreation())
            .build();

        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType("User")
                .aggregateId("usr_" + user.getId())
                .eventType(USER_REGISTERED_TOPIC)
                .payload(objectMapper.writeValueAsString(event))
                .correlationId(correlationId)
                .build();

            outboxRepository.save(outboxEvent);
            LOG.debug("Saved user.registered event to outbox: {}", outboxEvent.getId());
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize user.registered event", e);
            throw new RuntimeException("Failed to publish user.registered event", e);
        }
    }

    /**
     * Publishes an otp.requested event when an OTP needs to be sent.
     * This triggers SMS delivery in NotificationMS.
     *
     * @param phoneNumber the phone number to send OTP to
     * @param otpCode the generated OTP code
     * @param purpose the purpose of the OTP
     * @param correlationId the correlation ID for request tracing
     */
    @Transactional
    public void publishOtpRequested(String phoneNumber, String otpCode, OtpPurpose purpose, String correlationId) {
        LOG.info("Publishing otp.requested event for phone: {} with purpose: {}", 
            maskPhoneNumber(phoneNumber), purpose);

        OtpRequestedEvent event = OtpRequestedEvent.builder()
            .phoneNumber(phoneNumber)
            .otpCode(otpCode)
            .purpose(purpose)
            .expiresAt(Instant.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES))
            .locale("fr")
            .build();

        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType("Otp")
                .aggregateId(phoneNumber)
                .eventType(OTP_REQUESTED_TOPIC)
                .payload(objectMapper.writeValueAsString(event))
                .correlationId(correlationId)
                .build();

            outboxRepository.save(outboxEvent);
            LOG.debug("Saved otp.requested event to outbox: {}", outboxEvent.getId());
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize otp.requested event", e);
            throw new RuntimeException("Failed to publish otp.requested event", e);
        }
    }

    /**
     * Convenience method for publishing OTP request during registration.
     */
    @Transactional
    public void publishOtpForRegistration(String phoneNumber, String otpCode, String correlationId) {
        publishOtpRequested(phoneNumber, otpCode, OtpPurpose.REGISTRATION, correlationId);
    }

    /**
     * Convenience method for publishing OTP request during PIN reset.
     */
    @Transactional
    public void publishOtpForPinReset(String phoneNumber, String otpCode, String correlationId) {
        publishOtpRequested(phoneNumber, otpCode, OtpPurpose.PIN_RESET, correlationId);
    }

    /**
     * Convenience method for publishing OTP request during PIN creation.
     */
    @Transactional
    public void publishOtpForPinCreation(String phoneNumber, String otpCode, String correlationId) {
        publishOtpRequested(phoneNumber, otpCode, OtpPurpose.PIN_CREATION, correlationId);
    }

    /**
     * Masks phone number for logging (shows only last 4 digits).
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
