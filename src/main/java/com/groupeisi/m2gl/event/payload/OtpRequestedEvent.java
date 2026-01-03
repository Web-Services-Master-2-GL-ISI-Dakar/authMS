package com.groupeisi.m2gl.event.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Event payload for OTP request.
 * Published when an OTP needs to be sent to a user.
 * Consumed by NotificationMS to send SMS.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtpRequestedEvent {

    private String phoneNumber;
    private String otpCode;
    private OtpPurpose purpose;
    private Instant expiresAt;
    private String locale;

    public OtpRequestedEvent() {}

    private OtpRequestedEvent(Builder builder) {
        this.phoneNumber = builder.phoneNumber;
        this.otpCode = builder.otpCode;
        this.purpose = builder.purpose;
        this.expiresAt = builder.expiresAt;
        this.locale = builder.locale;
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

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(OtpPurpose purpose) {
        this.purpose = purpose;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Purpose of the OTP request.
     */
    public enum OtpPurpose {
        REGISTRATION,
        PIN_RESET,
        PIN_CREATION,
        TRANSACTION_VERIFICATION
    }

    public static class Builder {
        private String phoneNumber;
        private String otpCode;
        private OtpPurpose purpose;
        private Instant expiresAt;
        private String locale = "fr";

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder otpCode(String otpCode) {
            this.otpCode = otpCode;
            return this;
        }

        public Builder purpose(OtpPurpose purpose) {
            this.purpose = purpose;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder locale(String locale) {
            this.locale = locale;
            return this;
        }

        public OtpRequestedEvent build() {
            return new OtpRequestedEvent(this);
        }
    }
}
