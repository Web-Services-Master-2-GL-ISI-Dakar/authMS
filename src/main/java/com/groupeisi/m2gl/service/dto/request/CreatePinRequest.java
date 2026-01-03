package com.groupeisi.m2gl.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating initial PIN.
 */
public class CreatePinRequest {

    @NotBlank(message = "PIN is required")
    @Size(min = 4, max = 4, message = "PIN must be 4 digits")
    @Pattern(regexp = "^[0-9]{4}$", message = "PIN must contain only digits")
    private String pin;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must contain only digits")
    private String otp;

    public CreatePinRequest() {}

    public CreatePinRequest(String pin, String otp) {
        this.pin = pin;
        this.otp = otp;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
