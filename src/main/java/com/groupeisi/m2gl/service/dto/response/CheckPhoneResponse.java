package com.groupeisi.m2gl.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Response DTO for phone number check endpoint.
 * Returns whether the user is new and if OTP was sent.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckPhoneResponse {

    private String phoneNumber;
    private boolean isNewUser;
    private boolean otpSent;
    private Instant otpExpiresAt;

    public CheckPhoneResponse() {}

    private CheckPhoneResponse(Builder builder) {
        this.phoneNumber = builder.phoneNumber;
        this.isNewUser = builder.isNewUser;
        this.otpSent = builder.otpSent;
        this.otpExpiresAt = builder.otpExpiresAt;
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

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
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

    public static class Builder {
        private String phoneNumber;
        private boolean isNewUser;
        private boolean otpSent;
        private Instant otpExpiresAt;

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder isNewUser(boolean isNewUser) {
            this.isNewUser = isNewUser;
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

        public CheckPhoneResponse build() {
            return new CheckPhoneResponse(this);
        }
    }
}
