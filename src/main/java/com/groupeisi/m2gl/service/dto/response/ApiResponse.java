package com.groupeisi.m2gl.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

/**
 * Generic API response wrapper following OND Money contract.
 * All REST endpoints should return this envelope format.
 *
 * @param <T> the type of data payload
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private Meta meta;

    public ApiResponse() {}

    private ApiResponse(boolean success, T data, Meta meta) {
        this.success = success;
        this.data = data;
        this.meta = meta;
    }

    /**
     * Creates a successful response with data.
     *
     * @param data the response payload
     * @param correlationId the correlation ID for request tracing
     * @param <T> the type of data
     * @return the API response
     */
    public static <T> ApiResponse<T> success(T data, String correlationId) {
        return new ApiResponse<>(
            true,
            data,
            Meta.builder()
                .timestamp(Instant.now())
                .correlationId(correlationId)
                .build()
        );
    }

    /**
     * Creates a successful response without data.
     *
     * @param correlationId the correlation ID for request tracing
     * @return the API response
     */
    public static ApiResponse<Void> success(String correlationId) {
        return new ApiResponse<>(
            true,
            null,
            Meta.builder()
                .timestamp(Instant.now())
                .correlationId(correlationId)
                .build()
        );
    }

    // Getters and setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * Response metadata including timestamp and correlation ID.
     */
    public static class Meta {
        private Instant timestamp;
        private String correlationId;

        public Meta() {}

        private Meta(Instant timestamp, String correlationId) {
            this.timestamp = timestamp;
            this.correlationId = correlationId;
        }

        public static MetaBuilder builder() {
            return new MetaBuilder();
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        public static class MetaBuilder {
            private Instant timestamp;
            private String correlationId;

            public MetaBuilder timestamp(Instant timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public MetaBuilder correlationId(String correlationId) {
                this.correlationId = correlationId;
                return this;
            }

            public Meta build() {
                return new Meta(timestamp, correlationId);
            }
        }
    }
}
