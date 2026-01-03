package com.groupeisi.m2gl.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * PIN status response DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PinStatusResponse {

    private boolean hasPinConfigured;
    private Instant pinUpdatedAt;
    private String message;

    public PinStatusResponse() {}

    private PinStatusResponse(Builder builder) {
        this.hasPinConfigured = builder.hasPinConfigured;
        this.pinUpdatedAt = builder.pinUpdatedAt;
        this.message = builder.message;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public boolean isHasPinConfigured() {
        return hasPinConfigured;
    }

    public void setHasPinConfigured(boolean hasPinConfigured) {
        this.hasPinConfigured = hasPinConfigured;
    }

    public Instant getPinUpdatedAt() {
        return pinUpdatedAt;
    }

    public void setPinUpdatedAt(Instant pinUpdatedAt) {
        this.pinUpdatedAt = pinUpdatedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Builder {
        private boolean hasPinConfigured;
        private Instant pinUpdatedAt;
        private String message;

        public Builder hasPinConfigured(boolean hasPinConfigured) {
            this.hasPinConfigured = hasPinConfigured;
            return this;
        }

        public Builder pinUpdatedAt(Instant pinUpdatedAt) {
            this.pinUpdatedAt = pinUpdatedAt;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public PinStatusResponse build() {
            return new PinStatusResponse(this);
        }
    }
}
