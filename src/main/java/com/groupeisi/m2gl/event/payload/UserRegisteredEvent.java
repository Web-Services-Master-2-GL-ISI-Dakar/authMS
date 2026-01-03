package com.groupeisi.m2gl.event.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Event payload for user registration.
 * Published when a new user completes registration.
 * Consumed by TxEngineMS to create wallet.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegisteredEvent {

    private String userId;
    private String keycloakId;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private Instant registeredAt;

    public UserRegisteredEvent() {}

    private UserRegisteredEvent(Builder builder) {
        this.userId = builder.userId;
        this.keycloakId = builder.keycloakId;
        this.phoneNumber = builder.phoneNumber;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.registeredAt = builder.registeredAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public static class Builder {
        private String userId;
        private String keycloakId;
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private String email;
        private Instant registeredAt;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder keycloakId(String keycloakId) {
            this.keycloakId = keycloakId;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder registeredAt(Instant registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        public UserRegisteredEvent build() {
            return new UserRegisteredEvent(this);
        }
    }
}
