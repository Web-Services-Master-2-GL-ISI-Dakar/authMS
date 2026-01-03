package com.groupeisi.m2gl.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for confirming PIN reset with OTP.
 */
public class ConfirmPinResetRequest {

    @NotBlank(message = "Phone number is required")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format")
    private String phoneNumber;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must contain only digits")
    private String otp;

    @NotBlank(message = "New PIN is required")
    @Size(min = 4, max = 4, message = "New PIN must be 4 digits")
    @Pattern(regexp = "^[0-9]{4}$", message = "New PIN must contain only digits")
    private String newPin;

    public ConfirmPinResetRequest() {}

    public ConfirmPinResetRequest(String phoneNumber, String otp, String newPin) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.newPin = newPin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }
}
