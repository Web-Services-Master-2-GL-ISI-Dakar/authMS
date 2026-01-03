package com.groupeisi.m2gl.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating existing PIN.
 */
public class UpdatePinRequest {

    @NotBlank(message = "Current PIN is required")
    @Size(min = 4, max = 4, message = "Current PIN must be 4 digits")
    @Pattern(regexp = "^[0-9]{4}$", message = "Current PIN must contain only digits")
    private String currentPin;

    @NotBlank(message = "New PIN is required")
    @Size(min = 4, max = 4, message = "New PIN must be 4 digits")
    @Pattern(regexp = "^[0-9]{4}$", message = "New PIN must contain only digits")
    private String newPin;

    public UpdatePinRequest() {}

    public UpdatePinRequest(String currentPin, String newPin) {
        this.currentPin = currentPin;
        this.newPin = newPin;
    }

    public String getCurrentPin() {
        return currentPin;
    }

    public void setCurrentPin(String currentPin) {
        this.currentPin = currentPin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }
}
