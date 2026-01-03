package com.groupeisi.m2gl.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Response DTO for OTP resend endpoint.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResendOtpResponse {

    private String phoneNumber;
    private boolean otpSent;
    private Instant otpExpiresAt;
    private int remainingAttempts;
    private Integer retryAfterSeconds;

    public ResendOtpResponse() {}

    private ResendOtpResponse(Builder builder) {
        this.phoneNumber = builder.phoneNumber;
        this.otpSent = builder.otpSent;
        this.otpExpiresAt = builder.otpExpiresAt;
        this.remainingAttempts = builder.remainingAttempts;
        this.retryAfterSeconds = builder.retryAfterSeconds;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isOtpSent() {
        return otpSent;
    }

    public void setOtpSent(boolean otpSent) {
        this.otpSent = otpSent;
    }

    public Instant getOtpExpiresAt() {
        return otpExpiresAt;
    }

    public void setOtpExpiresAt(Instant otpExpiresAt) {
        this.otpExpiresAt = otpExpiresAt;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public void setRemainingAttempts(int remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }

    public Integer getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public void setRetryAfterSeconds(Integer retryAfterSeconds) {
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public static class Builder {
        private String phoneNumber;
        private boolean otpSent;
        private Instant otpExpiresAt;
        private int remainingAttempts;
        private Integer retryAfterSeconds;

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder otpSent(boolean otpSent) {
            this.otpSent = otpSent;
            return this;
        }

        public Builder otpExpiresAt(Instant otpExpiresAt) {
            this.otpExpiresAt = otpExpiresAt;
            return this;
        }

        public Builder remainingAttempts(int remainingAttempts) {
            this.remainingAttempts = remainingAttempts;
            return this;
        }

        public Builder retryAfterSeconds(Integer retryAfterSeconds) {
            this.retryAfterSeconds = retryAfterSeconds;
            return this;
        }

        public ResendOtpResponse build() {
            return new ResendOtpResponse(this);
        }
    }
}
