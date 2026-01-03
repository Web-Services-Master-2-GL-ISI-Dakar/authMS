package com.groupeisi.m2gl.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * OAuth2 tokens response DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokensResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private int expiresIn;

    public TokensResponse() {}

    private TokensResponse(Builder builder) {
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.tokenType = builder.tokenType;
        this.expiresIn = builder.expiresIn;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public static class Builder {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private int expiresIn;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder expiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public TokensResponse build() {
            return new TokensResponse(this);
        }
    }
}
