# AuthMS - Documentation des Endpoints API

> **Base URL:** `http://localhost:8023`  
> **Format:** JSON  
> **Méthode HTTP:** POST (sauf indication contraire)

---

## Table des matières

1. [Inscription](#1-inscription)
   - [Vérifier numéro (check-number)](#11-vérifier-numéro-check-number)
   - [Compléter inscription (completion)](#12-compléter-inscription-completion)
2. [Authentification](#2-authentification)
   - [Connexion (login)](#21-connexion-login)
3. [Réinitialisation PIN](#3-réinitialisation-pin)
   - [Démarrer réinitialisation (start)](#31-démarrer-réinitialisation-start)
   - [Confirmer réinitialisation (confirm)](#32-confirmer-réinitialisation-confirm)
4. [OTP (Legacy)](#4-otp-legacy)
   - [Générer OTP](#41-générer-otp)
   - [Vérifier OTP](#42-vérifier-otp)
5. [Utilisateurs](#5-utilisateurs)
   - [Liste des utilisateurs](#51-liste-des-utilisateurs)

---

## 1. Inscription

### 1.1 Vérifier numéro (check-number)

Vérifie si un numéro de téléphone existe déjà et génère un OTP pour les nouveaux utilisateurs.

**Endpoint:** `POST /api/inscription/check-number`

**Request Body:**
```json
{
  "numeroTelephone": "221771234567"
}
```

| Champ | Type | Obligatoire | Validation |
|-------|------|-------------|------------|
| `numeroTelephone` | string | ✅ | Max 15 caractères |

**Response (200 OK):**
```json
{
  "numeroTelephone": "221771234567",
  "otp": "123456",
  "nouveauUtilisateur": true
}
```

| Champ | Type | Description |
|-------|------|-------------|
| `numeroTelephone` | string | Numéro de téléphone |
| `otp` | string | Code OTP généré (DEV uniquement) |
| `nouveauUtilisateur` | boolean | `true` si nouvel utilisateur, `false` sinon |

---

### 1.2 Compléter inscription (completion)

Finalise l'inscription d'un nouvel utilisateur avec vérification OTP, création PIN et informations KYC.

**Endpoint:** `POST /api/inscription/completion`

**Request Body:**
```json
{
  "numeroTelephone": "221771234567",
  "otp": "123456",
  "pin": "1234",
  "prenom": "Mamadou",
  "nom": "Diallo",
  "email": "mamadou.diallo@email.com"
}
```

| Champ | Type | Obligatoire | Validation |
|-------|------|-------------|------------|
| `numeroTelephone` | string | ✅ | - |
| `otp` | string | ✅ | - |
| `pin` | string | ✅ | 4 chiffres |
| `prenom` | string | ✅ | Max 100 caractères |
| `nom` | string | ✅ | Max 100 caractères |
| `email` | string | ✅ | Format email, max 150 caractères |

**Response (200 OK):**
```json
"Inscription réussie"
```

**Erreurs possibles:**
- `400 Bad Request` - OTP invalide ou expiré
- `400 Bad Request` - Utilisateur déjà existant

---

## 2. Authentification

### 2.1 Connexion (login)

Authentifie un utilisateur existant avec son numéro de téléphone et PIN.

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "numeroTelephone": "221771234567",
  "pin": "1234"
}
```

| Champ | Type | Obligatoire | Validation |
|-------|------|-------------|------------|
| `numeroTelephone` | string | ✅ | - |
| `pin` | string | ✅ | 4 chiffres |

**Response (200 OK):**
```json
{
  "succes": true,
  "message": "Connexion réussie",
  "keycloakId": "uuid-keycloak-user-id"
}
```

| Champ | Type | Description |
|-------|------|-------------|
| `succes` | boolean | Statut de la connexion |
| `message` | string | Message descriptif |
| `keycloakId` | string | Identifiant Keycloak de l'utilisateur |

**Erreurs possibles:**
- `401 Unauthorized` - PIN incorrect
- `404 Not Found` - Utilisateur non trouvé

---

## 3. Réinitialisation PIN

### 3.1 Démarrer réinitialisation (start)

Initie le processus de réinitialisation du PIN en envoyant un OTP.

**Endpoint:** `POST /api/pin/reset/start`

**Request Body:**
```json
{
  "numeroTelephone": "221771234567"
}
```

| Champ | Type | Obligatoire |
|-------|------|-------------|
| `numeroTelephone` | string | ✅ |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "OTP envoyé pour réinitialisation du PIN",
  "otp": "789123"
}
```

| Champ | Type | Description |
|-------|------|-------------|
| `success` | boolean | Statut de l'opération |
| `message` | string | Message descriptif |
| `otp` | string | Code OTP (DEV uniquement) |

**Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Utilisateur inexistant. Veuillez vous inscrire."
}
```

---

### 3.2 Confirmer réinitialisation (confirm)

Confirme la réinitialisation du PIN avec l'OTP et le nouveau PIN.

**Endpoint:** `POST /api/pin/reset/confirm`

**Request Body:**
```json
{
  "numeroTelephone": "221771234567",
  "otp": "789123",
  "nouveauPin": "5678"
}
```

| Champ | Type | Obligatoire |
|-------|------|-------------|
| `numeroTelephone` | string | ✅ |
| `otp` | string | ✅ |
| `nouveauPin` | string | ✅ |

**Response (200 OK):**
```json
{
  "success": true,
  "message": "PIN réinitialisé avec succès !"
}
```

**Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "OTP invalide ou utilisateur inexistant"
}
```

---

## 4. OTP (Legacy)

> ⚠️ **Note:** Ces endpoints sont des APIs de bas niveau. Utilisez de préférence les endpoints d'inscription et de réinitialisation PIN.

### 4.1 Générer OTP

Génère un code OTP pour un numéro de téléphone.

**Endpoint:** `POST /api/otp/generer`

**Request Body:**
```json
{
  "numeroTelephone": "221771234567"
}
```

**Response (200 OK):**
```json
{
  "numeroTelephone": "221771234567",
  "otp": "456789",
  "nouveauUtilisateur": false
}
```

---

### 4.2 Vérifier OTP

Vérifie un code OTP et optionnellement définit le PIN.

**Endpoint:** `POST /api/otp/verifier`

**Request Body:**
```json
{
  "numeroTelephone": "221771234567",
  "otp": "456789",
  "pin": "1234"
}
```

| Champ | Type | Obligatoire | Description |
|-------|------|-------------|-------------|
| `numeroTelephone` | string | ✅ | - |
| `otp` | string | ✅ | - |
| `pin` | string | ❌ | Optionnel, définit le PIN si fourni |

**Response (200 OK):**
```json
"OTP validé, vous pouvez maintenant définir votre PIN."
```

**Response (400 Bad Request):**
```json
"OTP invalide ou expiré"
```

---

## 5. Utilisateurs

### 5.1 Liste des utilisateurs

Récupère la liste paginée des utilisateurs publics.

**Endpoint:** `GET /api/users`

**Query Parameters:**
| Paramètre | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Numéro de page (0-based) |
| `size` | int | 20 | Nombre d'éléments par page |
| `sort` | string | - | Champ de tri (ex: `id,asc`) |

**Response Headers:**
| Header | Description |
|--------|-------------|
| `X-Total-Count` | Nombre total d'éléments |

**Response (200 OK):**
```json
[
  {
    "id": "uuid",
    "login": "mamadou.diallo",
    "firstName": "Mamadou",
    "lastName": "Diallo"
  }
]
```

---

## Codes d'erreur HTTP

| Code | Description |
|------|-------------|
| `200` | Succès |
| `400` | Requête invalide (validation échouée) |
| `401` | Non authentifié |
| `403` | Accès refusé |
| `404` | Ressource non trouvée |
| `500` | Erreur serveur |

---

## Flux d'authentification

### Nouvel utilisateur (Inscription)

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  check-number   │────▶│   OTP envoyé    │────▶│   completion    │
│                 │     │  (nouveauUtili- │     │  (OTP + PIN +   │
│                 │     │   sateur=true)  │     │     KYC)        │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

### Utilisateur existant (Connexion)

```
┌─────────────────┐     ┌─────────────────┐
│  check-number   │────▶│     login       │
│                 │     │  (nouveauUtili- │
│                 │     │   sateur=false) │
└─────────────────┘     └─────────────────┘
```

### Réinitialisation PIN

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  reset/start    │────▶│   OTP envoyé    │────▶│  reset/confirm  │
│                 │     │                 │     │ (OTP + newPIN)  │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

---

## Exemples cURL

### Inscription - Vérifier numéro
```bash
curl -X POST http://localhost:8023/api/inscription/check-number \
  -H "Content-Type: application/json" \
  -d '{"numeroTelephone": "221771234567"}'
```

### Inscription - Complétion
```bash
curl -X POST http://localhost:8023/api/inscription/completion \
  -H "Content-Type: application/json" \
  -d '{
    "numeroTelephone": "221771234567",
    "otp": "123456",
    "pin": "1234",
    "prenom": "Mamadou",
    "nom": "Diallo",
    "email": "mamadou@email.com"
  }'
```

### Connexion
```bash
curl -X POST http://localhost:8023/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "numeroTelephone": "221771234567",
    "pin": "1234"
  }'
```

### Réinitialisation PIN - Démarrage
```bash
curl -X POST http://localhost:8023/api/pin/reset/start \
  -H "Content-Type: application/json" \
  -d '{"numeroTelephone": "221771234567"}'
```

### Réinitialisation PIN - Confirmation
```bash
curl -X POST http://localhost:8023/api/pin/reset/confirm \
  -H "Content-Type: application/json" \
  -d '{
    "numeroTelephone": "221771234567",
    "otp": "789123",
    "nouveauPin": "5678"
  }'
```

---

## Configuration CORS

Le service accepte les requêtes des origines suivantes en mode développement :
- `http://localhost:8081` (Expo)
- `http://localhost:19006` (Expo Web)
- `http://localhost:19000` (Expo)
- `http://localhost:3000` (Web)
- `exp://localhost:8081` (Expo Go)

---

*Dernière mise à jour : Janvier 2026*
