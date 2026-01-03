# Authentication Service (AuthMS) - API Documentation

> **Base URL:** `http://localhost:8023`  
> **API Version:** v1  
> **Prefix:** `/api/v1`  
> **Format:** JSON  
> **Authentication:** Bearer Token (JWT) where indicated

---

## Table of Contents

1. [Sign-Up](#1-sign-up)
   - [Check Phone Number](#11-check-phone-number)
   - [Complete Registration](#12-complete-registration)
2. [Authentication](#2-authentication)
   - [Login](#21-login)
   - [Refresh Token](#22-refresh-token)
   - [Logout](#23-logout)
3. [PIN Management](#3-pin-management)
   - [Check PIN Status](#31-check-pin-status)
   - [Create PIN](#32-create-pin)
   - [Update PIN](#33-update-pin)
   - [Reset PIN - Initiate](#34-reset-pin---initiate)
   - [Reset PIN - Confirm](#35-reset-pin---confirm)
4. [User Profile](#4-user-profile)
   - [Get Current User](#41-get-current-user)
   - [Update Profile](#42-update-profile)
5. [Data Models](#5-data-models)
6. [Error Codes](#6-error-codes)

---

## 1. Sign-Up

### 1.1 Check Phone Number

Verifies if a phone number is already registered. For new users, initiates OTP delivery.

**Endpoint:** `POST /api/v1/auth/sign-up/check-phone`

**Authentication:** None (Public)

**Request Body:**
```json
{
  "phoneNumber": "+221771234567"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `phoneNumber` | string | ✅ | E.164 format, max 15 chars |

**Response (200 OK) - New User:**
```json
{
  "success": true,
  "data": {
    "phoneNumber": "+221771234567",
    "isNewUser": true,
    "otpSent": true,
    "otpExpiresAt": "2026-01-03T12:05:00Z"
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Response (200 OK) - Existing User:**
```json
{
  "success": true,
  "data": {
    "phoneNumber": "+221771234567",
    "isNewUser": false,
    "otpSent": false
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Events Published:**
- `otp.requested` → NotificationMS (for new users only)

---

### 1.2 Complete Registration

Finalizes user registration with OTP verification, PIN creation, and KYC information.

**Endpoint:** `POST /api/v1/auth/sign-up/complete`

**Authentication:** None (Public)

**Request Body:**
```json
{
  "phoneNumber": "+221771234567",
  "otp": "123456",
  "pin": "1234",
  "firstName": "Mamadou",
  "lastName": "Diallo",
  "email": "mamadou.diallo@email.com"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `phoneNumber` | string | ✅ | E.164 format |
| `otp` | string | ✅ | 6 digits |
| `pin` | string | ✅ | 4 digits |
| `firstName` | string | ✅ | 2-100 chars, letters only |
| `lastName` | string | ✅ | 2-100 chars, letters only |
| `email` | string | ✅ | Valid email, max 150 chars |

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_550e8400-e29b-41d4-a716-446655440000",
      "keycloakId": "kc_662f9c12-abcd-4321-9876-fedcba987654",
      "phoneNumber": "+221771234567",
      "firstName": "Mamadou",
      "lastName": "Diallo",
      "email": "mamadou.diallo@email.com",
      "status": "ACTIVE",
      "hasPinConfigured": true,
      "createdAt": "2026-01-03T12:00:00Z"
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "expiresIn": 900
    }
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Events Published:**
- `user.registered` → TxEngineMS (triggers wallet creation)

**Error Responses:**

| Status | Code | Description |
|--------|------|-------------|
| 400 | `INVALID_OTP` | OTP is invalid or expired |
| 400 | `INVALID_PIN_FORMAT` | PIN must be 4 digits |
| 409 | `USER_ALREADY_EXISTS` | Phone number already registered |
| 422 | `OTP_MAX_ATTEMPTS` | Maximum OTP attempts exceeded |

---

## 2. Authentication

### 2.1 Login

Authenticates a user with phone number and PIN.

**Endpoint:** `POST /api/v1/auth/login`

**Authentication:** None (Public)

**Request Body:**
```json
{
  "phoneNumber": "+221771234567",
  "pin": "1234"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `phoneNumber` | string | ✅ | E.164 format |
| `pin` | string | ✅ | 4 digits |

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_550e8400-e29b-41d4-a716-446655440000",
      "keycloakId": "kc_662f9c12-abcd-4321-9876-fedcba987654",
      "phoneNumber": "+221771234567",
      "firstName": "Mamadou",
      "lastName": "Diallo",
      "email": "mamadou.diallo@email.com",
      "status": "ACTIVE",
      "hasPinConfigured": true,
      "lastLoginAt": "2026-01-03T12:00:00Z"
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "expiresIn": 900
    }
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Error Responses:**

| Status | Code | Description |
|--------|------|-------------|
| 401 | `INVALID_CREDENTIALS` | Phone number or PIN is incorrect |
| 404 | `USER_NOT_FOUND` | No user found with this phone number |
| 423 | `ACCOUNT_LOCKED` | Account temporarily locked (too many attempts) |
| 403 | `ACCOUNT_DISABLED` | Account has been disabled |

---

### 2.2 Refresh Token

Refreshes an expired access token using a valid refresh token.

**Endpoint:** `POST /api/v1/auth/refresh`

**Authentication:** None (Public)

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "tokens": {
      "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "expiresIn": 900
    }
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Error Responses:**

| Status | Code | Description |
|--------|------|-------------|
| 401 | `INVALID_REFRESH_TOKEN` | Refresh token is invalid or expired |
| 401 | `TOKEN_REVOKED` | Refresh token has been revoked |

---

### 2.3 Logout

Invalidates the current session and revokes tokens.

**Endpoint:** `POST /api/v1/auth/logout`

**Authentication:** Bearer Token ✅

**Request Headers:**
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Request Body (optional):**
```json
{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (204 No Content)**

---

## 3. PIN Management

### 3.1 Check PIN Status

Checks whether the authenticated user has a PIN configured.

**Endpoint:** `GET /api/v1/auth/pin`

**Authentication:** Bearer Token ✅

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "hasPinConfigured": true,
    "pinUpdatedAt": "2026-01-01T10:00:00Z"
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

---

### 3.2 Create PIN

Creates an initial PIN for a user who doesn't have one. Requires OTP verification.

**Endpoint:** `POST /api/v1/auth/pin`

**Authentication:** Bearer Token ✅

**Request Body:**
```json
{
  "pin": "1234",
  "otp": "123456"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `pin` | string | ✅ | 4 digits |
| `otp` | string | ✅ | 6 digits (from prior OTP request) |

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "message": "PIN created successfully",
    "hasPinConfigured": true
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Error Responses:**

| Status | Code | Description |
|--------|------|-------------|
| 400 | `INVALID_OTP` | OTP is invalid or expired |
| 400 | `INVALID_PIN_FORMAT` | PIN must be 4 digits |
| 409 | `PIN_ALREADY_EXISTS` | User already has a PIN configured |

---

### 3.3 Update PIN

Updates an existing PIN. Requires current PIN for verification.

**Endpoint:** `PUT /api/v1/auth/pin`

**Authentication:** Bearer Token ✅

**Request Body:**
```json
{
  "currentPin": "1234",
  "newPin": "5678"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `currentPin` | string | ✅ | 4 digits |
| `newPin` | string | ✅ | 4 digits, different from current |

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "message": "PIN updated successfully",
    "pinUpdatedAt": "2026-01-03T12:00:00Z"
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Error Responses:**

| Status | Code | Description |
|--------|------|-------------|
| 400 | `INVALID_PIN_FORMAT` | PIN must be 4 digits |
| 401 | `INVALID_CURRENT_PIN` | Current PIN is incorrect |
| 422 | `PIN_SAME_AS_CURRENT` | New PIN must be different |

---

### 3.4 Reset PIN - Initiate

Initiates PIN reset process by sending an OTP to the user's phone.

**Endpoint:** `POST /api/v1/auth/pin/reset`

**Authentication:** None (Public)

**Request Body:**
```json
{
  "phoneNumber": "+221771234567"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "phoneNumber": "+221771234567",
    "otpSent": true,
    "otpExpiresAt": "2026-01-03T12:05:00Z"
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Events Published:**
- `otp.requested` → NotificationMS

**Error Responses:**

| Status | Code | Description |
|--------|------|-------------|
| 404 | `USER_NOT_FOUND` | No user found with this phone number |
| 429 | `TOO_MANY_REQUESTS` | Rate limit exceeded for PIN reset |

---

### 3.5 Reset PIN - Confirm

Confirms PIN reset with OTP verification and sets the new PIN.

**Endpoint:** `POST /api/v1/auth/pin/reset/confirm`

**Authentication:** None (Public)

**Request Body:**
```json
{
  "phoneNumber": "+221771234567",
  "otp": "789123",
  "newPin": "5678"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `phoneNumber` | string | ✅ | E.164 format |
| `otp` | string | ✅ | 6 digits |
| `newPin` | string | ✅ | 4 digits |

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "message": "PIN reset successfully",
    "pinUpdatedAt": "2026-01-03T12:00:00Z"
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

**Error Responses:**

| Status | Code | Description |
|--------|------|-------------|
| 400 | `INVALID_OTP` | OTP is invalid or expired |
| 400 | `INVALID_PIN_FORMAT` | PIN must be 4 digits |
| 404 | `USER_NOT_FOUND` | No user found with this phone number |
| 422 | `OTP_MAX_ATTEMPTS` | Maximum OTP attempts exceeded |

---

## 4. User Profile

### 4.1 Get Current User

Retrieves the profile of the authenticated user.

**Endpoint:** `GET /api/v1/users/me`

**Authentication:** Bearer Token ✅

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_550e8400-e29b-41d4-a716-446655440000",
      "keycloakId": "kc_662f9c12-abcd-4321-9876-fedcba987654",
      "phoneNumber": "+221771234567",
      "firstName": "Mamadou",
      "lastName": "Diallo",
      "email": "mamadou.diallo@email.com",
      "status": "ACTIVE",
      "hasPinConfigured": true,
      "createdAt": "2026-01-01T10:00:00Z",
      "lastLoginAt": "2026-01-03T12:00:00Z"
    }
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

---

### 4.2 Update Profile

Updates the profile information of the authenticated user.

**Endpoint:** `PUT /api/v1/users/me`

**Authentication:** Bearer Token ✅

**Request Body:**
```json
{
  "firstName": "Mamadou",
  "lastName": "Diallo",
  "email": "new.email@example.com"
}
```

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `firstName` | string | ❌ | 2-100 chars, letters only |
| `lastName` | string | ❌ | 2-100 chars, letters only |
| `email` | string | ❌ | Valid email, max 150 chars |

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "usr_550e8400-e29b-41d4-a716-446655440000",
      "keycloakId": "kc_662f9c12-abcd-4321-9876-fedcba987654",
      "phoneNumber": "+221771234567",
      "firstName": "Mamadou",
      "lastName": "Diallo",
      "email": "new.email@example.com",
      "status": "ACTIVE",
      "hasPinConfigured": true,
      "createdAt": "2026-01-01T10:00:00Z",
      "updatedAt": "2026-01-03T12:00:00Z"
    }
  },
  "meta": {
    "timestamp": "2026-01-03T12:00:00Z",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000"
  }
}
```

---

## 5. Data Models

### 5.1 User Object

```json
{
  "id": "string (UUID with usr_ prefix)",
  "keycloakId": "string (UUID with kc_ prefix)",
  "phoneNumber": "string (E.164 format)",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "status": "enum: PENDING | ACTIVE | SUSPENDED | DISABLED",
  "hasPinConfigured": "boolean",
  "createdAt": "string (ISO 8601)",
  "updatedAt": "string (ISO 8601)",
  "lastLoginAt": "string (ISO 8601)"
}
```

### 5.2 Tokens Object

```json
{
  "accessToken": "string (JWT)",
  "refreshToken": "string (JWT)",
  "tokenType": "string (always 'Bearer')",
  "expiresIn": "integer (seconds until expiry)"
}
```

### 5.3 User Status Enum

| Status | Description |
|--------|-------------|
| `PENDING` | Registration incomplete (awaiting OTP verification) |
| `ACTIVE` | Fully registered and active |
| `SUSPENDED` | Temporarily suspended (manual review) |
| `DISABLED` | Permanently disabled |

---

## 6. Error Codes

### 6.1 Standard Error Response

```json
{
  "timestamp": "2026-01-03T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "ERROR_CODE",
  "message": "Human-readable error description",
  "path": "/api/v1/auth/login",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "details": {}
}
```

### 6.2 Error Code Reference

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `VALIDATION_ERROR` | 400 | Request body validation failed |
| `INVALID_PHONE_FORMAT` | 400 | Phone number format invalid |
| `INVALID_PIN_FORMAT` | 400 | PIN must be exactly 4 digits |
| `INVALID_OTP` | 400 | OTP is invalid or expired |
| `INVALID_CREDENTIALS` | 401 | Phone or PIN is incorrect |
| `INVALID_CURRENT_PIN` | 401 | Current PIN verification failed |
| `INVALID_REFRESH_TOKEN` | 401 | Refresh token invalid or expired |
| `TOKEN_REVOKED` | 401 | Token has been revoked |
| `UNAUTHORIZED` | 401 | Authentication required |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `ACCOUNT_DISABLED` | 403 | Account has been disabled |
| `USER_NOT_FOUND` | 404 | User does not exist |
| `USER_ALREADY_EXISTS` | 409 | Phone number already registered |
| `PIN_ALREADY_EXISTS` | 409 | User already has a PIN |
| `OTP_MAX_ATTEMPTS` | 422 | Maximum OTP verification attempts exceeded |
| `PIN_SAME_AS_CURRENT` | 422 | New PIN must differ from current |
| `ACCOUNT_LOCKED` | 423 | Account temporarily locked |
| `TOO_MANY_REQUESTS` | 429 | Rate limit exceeded |
| `INTERNAL_ERROR` | 500 | Unexpected server error |

---

## 7. Authentication Flow Diagrams

### 7.1 New User Registration

```
┌───────────┐     ┌─────────────┐     ┌─────────┐     ┌───────────────┐
│  Client   │     │   AuthMS    │     │  Kafka  │     │ NotificationMS│
└─────┬─────┘     └──────┬──────┘     └────┬────┘     └───────┬───────┘
      │                  │                 │                  │
      │ POST /sign-up/check-phone          │                  │
      │─────────────────▶│                 │                  │
      │                  │                 │                  │
      │                  │ publish(otp.requested)             │
      │                  │────────────────▶│                  │
      │                  │                 │  consume         │
      │                  │                 │─────────────────▶│
      │                  │                 │                  │ Send SMS
      │◀─────────────────│                 │                  │
      │ { isNewUser: true, otpSent: true } │                  │
      │                  │                 │                  │
      │ [User receives OTP via SMS]        │                  │
      │                  │                 │                  │
      │ POST /sign-up/complete             │                  │
      │─────────────────▶│                 │                  │
      │                  │ Verify OTP      │                  │
      │                  │ Create Keycloak user               │
      │                  │ Save to DB      │                  │
      │                  │                 │                  │
      │                  │ publish(user.registered)           │
      │                  │────────────────▶│                  │
      │                  │                 │  [TxEngineMS consumes]
      │                  │                 │  → Create Wallet │
      │◀─────────────────│                 │                  │
      │ { user, tokens } │                 │                  │
```

### 7.2 Existing User Login

```
┌───────────┐     ┌─────────────┐     ┌──────────┐
│  Client   │     │   AuthMS    │     │ Keycloak │
└─────┬─────┘     └──────┬──────┘     └────┬─────┘
      │                  │                 │
      │ POST /sign-up/check-phone          │
      │─────────────────▶│                 │
      │◀─────────────────│                 │
      │ { isNewUser: false }               │
      │                  │                 │
      │ POST /login      │                 │
      │─────────────────▶│                 │
      │                  │ Verify PIN      │
      │                  │────────────────▶│
      │                  │                 │ Generate tokens
      │                  │◀────────────────│
      │◀─────────────────│                 │
      │ { user, tokens } │                 │
```

### 7.3 PIN Reset Flow

```
┌───────────┐     ┌─────────────┐     ┌─────────┐     ┌───────────────┐
│  Client   │     │   AuthMS    │     │  Kafka  │     │ NotificationMS│
└─────┬─────┘     └──────┬──────┘     └────┬────┘     └───────┬───────┘
      │                  │                 │                  │
      │ POST /pin/reset  │                 │                  │
      │─────────────────▶│                 │                  │
      │                  │ publish(otp.requested)             │
      │                  │────────────────▶│                  │
      │                  │                 │  consume         │
      │                  │                 │─────────────────▶│
      │                  │                 │                  │ Send SMS
      │◀─────────────────│                 │                  │
      │ { otpSent: true }│                 │                  │
      │                  │                 │                  │
      │ [User receives OTP]                │                  │
      │                  │                 │                  │
      │ POST /pin/reset/confirm            │                  │
      │─────────────────▶│                 │                  │
      │                  │ Verify OTP      │                  │
      │                  │ Update PIN hash │                  │
      │◀─────────────────│                 │                  │
      │ { success }      │                 │                  │
```

---

## 8. cURL Examples

### Check Phone Number
```bash
curl -X POST http://localhost:8023/api/v1/auth/sign-up/check-phone \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "+221771234567"}'
```

### Complete Registration
```bash
curl -X POST http://localhost:8023/api/v1/auth/sign-up/complete \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+221771234567",
    "otp": "123456",
    "pin": "1234",
    "firstName": "Mamadou",
    "lastName": "Diallo",
    "email": "mamadou@email.com"
  }'
```

### Login
```bash
curl -X POST http://localhost:8023/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+221771234567",
    "pin": "1234"
  }'
```

### Refresh Token
```bash
curl -X POST http://localhost:8023/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."}'
```

### Get Current User
```bash
curl -X GET http://localhost:8023/api/v1/users/me \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Update PIN
```bash
curl -X PUT http://localhost:8023/api/v1/auth/pin \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "currentPin": "1234",
    "newPin": "5678"
  }'
```

### Reset PIN - Initiate
```bash
curl -X POST http://localhost:8023/api/v1/auth/pin/reset \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "+221771234567"}'
```

### Reset PIN - Confirm
```bash
curl -X POST http://localhost:8023/api/v1/auth/pin/reset/confirm \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+221771234567",
    "otp": "789123",
    "newPin": "5678"
  }'
```

---

## 9. CORS Configuration

The service accepts requests from the following origins in development:

| Origin | Environment |
|--------|-------------|
| `http://localhost:8081` | Expo Dev |
| `http://localhost:19006` | Expo Web |
| `http://localhost:19000` | Expo |
| `http://localhost:3000` | Web Dev |
| `exp://localhost:8081` | Expo Go |

---

## Related Documentation

- [Backend Architecture](../../../docs/backend/ARCHITECTURE.md)
- [Event Contract (Kafka)](../../../docs/backend/EVENT_CONTRACT.md)

---

*Last updated: January 2026*
