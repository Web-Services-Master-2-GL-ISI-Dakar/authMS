package com.groupeisi.m2gl.security;

import org.springframework.stereotype.Service;

@Service
public class PinValidationService {

    public void verifierPin(String pin) {
        if (pin == null || pin.length() != 4) {
            throw new IllegalArgumentException("Le PIN doit contenir exactement 4 chiffres.");
        }
        if (pin.matches("(\\d)\\1{3}")) { // 1111, 2222, etc.
            throw new IllegalArgumentException("Le PIN ne peut pas contenir des chiffres identiques.");
        }
        if ("1234".equals(pin) || "0000".equals(pin)) {
            throw new IllegalArgumentException("Le PIN est trop simple, choisissez un autre.");
        }
    }
}
