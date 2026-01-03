package com.groupeisi.m2gl.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Authentication response DTO containing user data and tokens.
 * Used for login and registration completion responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private UserResponse user;
    private TokensResponse tokens;

    public AuthResponse() {}

    private AuthResponse(Builder builder) {
        this.user = builder.user;
        this.tokens = builder.tokens;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public TokensResponse getTokens() {
        return tokens;
    }

    public void setTokens(TokensResponse tokens) {
        this.tokens = tokens;
    }

    public static class Builder {
        private UserResponse user;
        private TokensResponse tokens;

        public Builder user(UserResponse user) {
            this.user = user;
            return this;
        }

        public Builder tokens(TokensResponse tokens) {
            this.tokens = tokens;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(this);
        }
    }
}
