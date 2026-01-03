package com.groupeisi.m2gl.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * User data response DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private String id;
    private String keycloakId;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private boolean hasPinConfigured;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

    public UserResponse() {}

    private UserResponse(Builder builder) {
        this.id = builder.id;
        this.keycloakId = builder.keycloakId;
        this.phoneNumber = builder.phoneNumber;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.status = builder.status;
        this.hasPinConfigured = builder.hasPinConfigured;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.lastLoginAt = builder.lastLoginAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHasPinConfigured() {
        return hasPinConfigured;
    }

    public void setHasPinConfigured(boolean hasPinConfigured) {
        this.hasPinConfigured = hasPinConfigured;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public static class Builder {
        private String id;
        private String keycloakId;
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private String email;
        private String status;
        private boolean hasPinConfigured;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant lastLoginAt;

        public Builder id(String id) {
            this.id = id;
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

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder hasPinConfigured(boolean hasPinConfigured) {
            this.hasPinConfigured = hasPinConfigured;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder lastLoginAt(Instant lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public UserResponse build() {
            return new UserResponse(this);
        }
    }
}
