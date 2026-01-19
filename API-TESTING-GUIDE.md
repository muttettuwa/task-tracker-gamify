# Task Tracker API - Complete Testing Guide

## 📋 Comprehensive Validation & Testing Documentation

This document outlines all validation combinations, test scenarios, and expected behaviors for the Task Tracker API with JWT authentication and role-based access control.

---

## 🎯 Testing Overview

### Test Coverage Summary

| Category | Test Cases | Pass Criteria |
|----------|-----------|---------------|
| Authentication | 11 | Valid/Invalid login scenarios |
| Authorization - Admin | 5 | SUPER_ADMIN access control |
| Authorization - User | 4 | USER role access control |
| Actuator | 2 | Public endpoints |
| Edge Cases & Security | 6 | Security & input validation |
| **TOTAL** | **28** | **Comprehensive coverage** |

---

## 1️⃣ Authentication Tests (11 Tests)

### 1.1 Login - Admin Success ✅

**Purpose:** Test successful admin login with valid credentials

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "admin@tasktracker.com",
    "password": "admin123"
}
```

**Expected Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "admin@tasktracker.com",
  "firstName": "Super",
  "lastName": "Admin",
  "roles": ["SUPER_ADMIN"],
  "organizationId": 1,
  "organizationName": "Default Organization"
}
```

**Validations:**
- ✅ Status code is 200
- ✅ Response time < 2000ms
- ✅ Token is present
- ✅ Token type is "Bearer"
- ✅ Token matches JWT format (3 parts separated by dots)
- ✅ Role includes "SUPER_ADMIN"
- ✅ Email matches request
- ✅ Organization data present
- ✅ Token saved to environment variable

---

### 1.2 Login - User Success ✅

**Purpose:** Test successful user login with valid credentials

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "user@tasktracker.com",
    "password": "user123"
}
```

**Expected Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 2,
  "email": "user@tasktracker.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["USER"],
  "organizationId": 1,
  "organizationName": "Default Organization"
}
```

**Validations:**
- ✅ Status code is 200
- ✅ Token is present
- ✅ Role includes "USER"
- ✅ Email is correct
- ✅ Token saved to environment variable

---

### 1.3 Login - Invalid Password ❌

**Purpose:** Test authentication failure with wrong password

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "admin@tasktracker.com",
    "password": "wrongpassword"
}
```

**Expected Response:** `401 Unauthorized`
```
Invalid email or password
```

**Validations:**
- ✅ Status code is 401
- ✅ Error message contains "Invalid email or password"
- ✅ No token returned

---

### 1.4 Login - Non-existent User ❌

**Purpose:** Test login with email that doesn't exist in database

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "nonexistent@example.com",
    "password": "anypassword"
}
```

**Expected Response:** `401 Unauthorized`
```
Invalid email or password
```

**Validations:**
- ✅ Status code is 401
- ✅ Error message is appropriate
- ✅ Doesn't reveal if user exists (security)

---

### 1.5 Login - Empty Email ❌

**Purpose:** Test Bean Validation for empty email field

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "",
    "password": "admin123"
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ Validation error: "Email is required" or "must not be blank"

**Bean Validation Rule:**
```java
@NotBlank(message = "Email is required")
private String email;
```

---

### 1.6 Login - Empty Password ❌

**Purpose:** Test Bean Validation for empty password field

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "admin@tasktracker.com",
    "password": ""
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ Validation error: "Password is required" or "must not be blank"

**Bean Validation Rule:**
```java
@NotBlank(message = "Password is required")
private String password;
```

---

### 1.7 Login - Invalid Email Format ❌

**Purpose:** Test @Email validation annotation

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "not-an-email",
    "password": "admin123"
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ Validation error: "Email must be valid" or "must be a well-formed email"

**Bean Validation Rule:**
```java
@Email(message = "Email must be valid")
private String email;
```

**Invalid Email Examples:**
- `not-an-email`
- `missing@domain`
- `@example.com`
- `user@`
- `user @example.com` (space)

---

### 1.8 Login - Missing Email Field ❌

**Purpose:** Test request with missing email field

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "password": "admin123"
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ Validation error for missing field

---

### 1.9 Login - Missing Password Field ❌

**Purpose:** Test request with missing password field

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "admin@tasktracker.com"
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ Validation error for missing field

---

### 1.10 Login - Empty Request Body ❌

**Purpose:** Test completely empty request

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ Multiple validation errors (both fields)

---

### 1.11 Login - Null Values ❌

**Purpose:** Test null values in request

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": null,
    "password": null
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ Validation errors for null values

---

## 2️⃣ Admin Endpoints Tests (5 Tests)

### 2.1 Admin Test - With Admin Token ✅

**Purpose:** Verify SUPER_ADMIN can access admin endpoints

**Request:**
```http
GET {{base_url}}/admin/test
Authorization: Bearer {{admin_token}}
```

**Expected Response:** `200 OK`
```json
{
  "message": "Welcome to admin area!",
  "user": "admin@tasktracker.com",
  "authorities": ["ROLE_SUPER_ADMIN"],
  "accessLevel": "SUPER_ADMIN only"
}
```

**Validations:**
- ✅ Status code is 200
- ✅ Welcome message present
- ✅ Access level is "SUPER_ADMIN"
- ✅ User email is correct

**Security Rule:**
```java
@PreAuthorize("hasRole('SUPER_ADMIN')")
```

---

### 2.2 Admin Test - With User Token ❌

**Purpose:** Verify USER role cannot access admin endpoints

**Request:**
```http
GET {{base_url}}/admin/test
Authorization: Bearer {{user_token}}
```

**Expected Response:** `403 Forbidden`

**Validations:**
- ✅ Status code is 403
- ✅ Access denied for regular user
- ✅ Role-based authorization working

**Security Behavior:**
- USER role has "ROLE_USER" authority
- Admin endpoint requires "ROLE_SUPER_ADMIN"
- Spring Security denies access
- Returns 403 Forbidden

---

### 2.3 Admin Test - No Token ❌

**Purpose:** Verify unauthenticated requests are rejected

**Request:**
```http
GET {{base_url}}/admin/test
```

**Expected Response:** `403 Forbidden`

**Validations:**
- ✅ Status code is 403
- ✅ Unauthorized without token

**Security Flow:**
1. No Authorization header
2. JWT filter doesn't set authentication
3. Spring Security denies access
4. Returns 403

---

### 2.4 Admin Test - Invalid Token ❌

**Purpose:** Verify invalid tokens are rejected

**Request:**
```http
GET {{base_url}}/admin/test
Authorization: Bearer invalid.token.here
```

**Expected Response:** `403 Forbidden`

**Validations:**
- ✅ Status code is 403
- ✅ Invalid token rejected

**Security Flow:**
1. JWT filter tries to validate token
2. Validation fails (invalid signature)
3. Authentication not set
4. Access denied

---

### 2.5 Admin Dashboard - With Admin Token ✅

**Purpose:** Test another admin endpoint

**Request:**
```http
GET {{base_url}}/admin/dashboard
Authorization: Bearer {{admin_token}}
```

**Expected Response:** `200 OK`
```json
{
  "message": "Admin Dashboard",
  "user": "admin@tasktracker.com",
  "description": "This is a protected admin endpoint"
}
```

**Validations:**
- ✅ Status code is 200
- ✅ Dashboard message present

---

## 3️⃣ User Endpoints Tests (4 Tests)

### 3.1 User Test - With User Token ✅

**Purpose:** Verify USER can access user endpoints

**Request:**
```http
GET {{base_url}}/user/test
Authorization: Bearer {{user_token}}
```

**Expected Response:** `200 OK`
```json
{
  "message": "Welcome to user area!",
  "user": "user@tasktracker.com",
  "authorities": ["ROLE_USER"],
  "accessLevel": "USER, SUPERVISOR, or SUPER_ADMIN"
}
```

**Validations:**
- ✅ Status code is 200
- ✅ Welcome message present
- ✅ User email is correct

**Security Rule:**
```java
@PreAuthorize("hasAnyRole('USER', 'SUPERVISOR', 'SUPER_ADMIN')")
```

---

### 3.2 User Test - With Admin Token ✅

**Purpose:** Verify hierarchical access (admin can access user endpoints)

**Request:**
```http
GET {{base_url}}/user/test
Authorization: Bearer {{admin_token}}
```

**Expected Response:** `200 OK`
```json
{
  "message": "Welcome to user area!",
  "user": "admin@tasktracker.com",
  "authorities": ["ROLE_SUPER_ADMIN"],
  "accessLevel": "USER, SUPERVISOR, or SUPER_ADMIN"
}
```

**Validations:**
- ✅ Status code is 200
- ✅ Admin can access user endpoints
- ✅ Hierarchical access working

**Access Matrix:**
| Endpoint | GUEST | USER | SUPERVISOR | SUPER_ADMIN |
|----------|-------|------|------------|-------------|
| /user/* | ❌ | ✅ | ✅ | ✅ |
| /admin/* | ❌ | ❌ | ❌ | ✅ |

---

### 3.3 User Test - No Token ❌

**Purpose:** Verify unauthenticated access denied

**Request:**
```http
GET {{base_url}}/user/test
```

**Expected Response:** `403 Forbidden`

**Validations:**
- ✅ Status code is 403
- ✅ Access denied without token

---

### 3.4 User Profile - With User Token ✅

**Purpose:** Test another user endpoint

**Request:**
```http
GET {{base_url}}/user/profile
Authorization: Bearer {{user_token}}
```

**Expected Response:** `200 OK`
```json
{
  "message": "User Profile",
  "user": "user@tasktracker.com",
  "description": "This endpoint is accessible by regular users"
}
```

**Validations:**
- ✅ Status code is 200
- ✅ Profile message present

---

## 4️⃣ Actuator Endpoints Tests (2 Tests)

### 4.1 Health Check - Public ✅

**Purpose:** Verify health endpoint is publicly accessible

**Request:**
```http
GET {{base_url}}/actuator/health
```

**Expected Response:** `200 OK`
```json
{
  "status": "UP"
}
```

**Validations:**
- ✅ Status code is 200
- ✅ Health status is "UP"
- ✅ No authentication required

**Security Configuration:**
```java
.requestMatchers("/actuator/health", "/actuator/info").permitAll()
```

---

### 4.2 Info - Public ✅

**Purpose:** Verify info endpoint is publicly accessible

**Request:**
```http
GET {{base_url}}/actuator/info
```

**Expected Response:** `200 OK`

**Validations:**
- ✅ Status code is 200
- ✅ No authentication required

---

## 5️⃣ Edge Cases & Security Tests (6 Tests)

### 5.1 SQL Injection Attempt - Email 🔒

**Purpose:** Verify SQL injection prevention

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "admin' OR '1'='1",
    "password": "admin123"
}
```

**Expected Response:** `400 Bad Request` or `401 Unauthorized`

**Validations:**
- ✅ Status code is 400 or 401 (not 200)
- ✅ SQL injection prevented
- ✅ No sensitive data leaked

**Security Measures:**
- JPA/Hibernate uses parameterized queries
- Email validation prevents SQL syntax
- No direct SQL execution from user input

---

### 5.2 XSS Attempt - Email 🔒

**Purpose:** Verify XSS prevention

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "<script>alert('xss')</script>",
    "password": "admin123"
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ XSS script not executed
- ✅ Response doesn't include `<script>` tag

**Security Measures:**
- @Email validation rejects script tags
- Spring Boot auto-escapes JSON responses
- Content-Type: application/json prevents browser execution

---

### 5.3 Very Long Email 🔒

**Purpose:** Test input length validation

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "verylongemailaddressthatexceedsreasonablelimits...@example.com",
    "password": "admin123"
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ Length validation working

**Database Constraint:**
```sql
email VARCHAR(255)
```

---

### 5.4 Special Characters in Password 🔒

**Purpose:** Verify special character handling

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "admin@tasktracker.com",
    "password": "!@#$%^&*()_+-={}[]|:;<>?,./~`"
}
```

**Expected Response:** `401 Unauthorized`

**Validations:**
- ✅ Status code is 401
- ✅ Special characters handled correctly
- ✅ No security vulnerabilities

**Note:** BCrypt securely hashes any password, including special characters

---

### 5.5 Whitespace Only Email 🔒

**Purpose:** Test @NotBlank validation

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "   ",
    "password": "admin123"
}
```

**Expected Response:** `400 Bad Request`

**Validations:**
- ✅ Status code is 400
- ✅ @NotBlank catches whitespace-only input

**@NotBlank vs @NotEmpty:**
- `@NotEmpty`: Allows whitespace
- `@NotBlank`: Rejects whitespace ✅

---

### 5.6 Case Sensitivity - Email Uppercase 🔍

**Purpose:** Test email case handling

**Request:**
```http
POST {{base_url}}/auth/login
Content-Type: application/json

{
    "email": "ADMIN@TASKTRACKER.COM",
    "password": "admin123"
}
```

**Expected Response:** `200 OK` or `401 Unauthorized`

**Validations:**
- ✅ Status code is 200 or 401
- ✅ Consistent behavior

**Implementation Note:**
- Current: Case-sensitive (exact match required)
- Enhancement: Add `.findByEmailIgnoreCase()` for case-insensitive login

---

## 📊 Test Execution Summary

### How to Run Tests

**Option 1: Postman Collection Runner**
1. Import `Task-Tracker-Complete.postman_collection.json`
2. Click "Run Collection"
3. View results in Postman Runner

**Option 2: Newman CLI**
```bash
npm install -g newman
newman run Task-Tracker-Complete.postman_collection.json
```

**Option 3: Bash Test Script**
```bash
./test-jwt-auth.sh
```

---

## 🎯 Expected Test Results

| Category | Total | Pass | Fail | Skip |
|----------|-------|------|------|------|
| Authentication | 11 | 2 | 9 | 0 |
| Admin Endpoints | 5 | 2 | 3 | 0 |
| User Endpoints | 4 | 3 | 1 | 0 |
| Actuator | 2 | 2 | 0 | 0 |
| Security | 6 | 0 | 6 | 0 |
| **TOTAL** | **28** | **9** | **19** | **0** |

**Pass:** Expected behavior (success responses)
**Fail:** Expected behavior (validation failures, security blocks)

---

## 🔐 Security Validation Matrix

| Attack Vector | Protection | Status |
|--------------|------------|--------|
| SQL Injection | Parameterized queries | ✅ Protected |
| XSS | Input validation, JSON escaping | ✅ Protected |
| CSRF | Stateless JWT (no cookies) | ✅ Protected |
| Brute Force | Rate limiting (todo) | ⚠️ Recommended |
| Session Hijacking | JWT expiration, HTTPS | ✅ Protected |
| Privilege Escalation | Role-based access control | ✅ Protected |

---

## 📝 Bean Validation Summary

### LoginRequest Validations

```java
@NotBlank(message = "Email is required")
@Email(message = "Email must be valid")
private String email;

@NotBlank(message = "Password is required")
private String password;
```

**Validation Combinations:**

| Email | Password | Result | Status Code |
|-------|----------|--------|-------------|
| Valid | Valid (correct) | Success | 200 |
| Valid | Valid (wrong) | Invalid credentials | 401 |
| Empty | Any | Validation error | 400 |
| Null | Any | Validation error | 400 |
| Whitespace | Any | Validation error | 400 |
| Invalid format | Any | Validation error | 400 |
| Any | Empty | Validation error | 400 |
| Any | Null | Validation error | 400 |

---

## 🚀 Quick Start Testing

### 1. Set Up Environment

In Postman, create environment with:
```
base_url: http://localhost:8080/api
admin_token: (auto-populated)
user_token: (auto-populated)
admin_userId: (auto-populated)
user_userId: (auto-populated)
```

### 2. Run in Order

Execute tests sequentially:
1. **Authentication** folder first (populates tokens)
2. **Admin Endpoints** (uses admin_token)
3. **User Endpoints** (uses both tokens)
4. **Actuator** (no auth needed)
5. **Edge Cases** (various scenarios)

### 3. Verify Results

Expected results:
- ✅ 9 successful authentications
- ✅ 19 properly rejected requests
- ✅ All tokens saved to environment
- ✅ All role-based access working

---

## 📚 Additional Resources

- **Postman Collection:** `Task-Tracker-Complete.postman_collection.json`
- **Test Script:** `test-jwt-auth.sh`
- **API Documentation:** `JWT-AUTHENTICATION-GUIDE.md`
- **Setup Guide:** `SETUP-GUIDE.md`

---

## ✅ Validation Checklist

Use this checklist to verify complete test coverage:

### Authentication
- [ ] Admin login success
- [ ] User login success
- [ ] Invalid password rejection
- [ ] Non-existent user rejection
- [ ] Empty email validation
- [ ] Empty password validation
- [ ] Invalid email format validation
- [ ] Missing email field validation
- [ ] Missing password field validation
- [ ] Empty request body validation
- [ ] Null values validation

### Authorization
- [ ] Admin can access admin endpoints
- [ ] User cannot access admin endpoints
- [ ] User can access user endpoints
- [ ] Admin can access user endpoints
- [ ] No token = access denied
- [ ] Invalid token = access denied

### Security
- [ ] SQL injection prevented
- [ ] XSS prevented
- [ ] Input length validation
- [ ] Special characters handled
- [ ] Whitespace validation
- [ ] Token format validation

---

**All 28 test scenarios documented and ready for execution!** ✅

Import the Postman collection and start testing! 🚀
