# 🧪 User Registration - Test Plan

## Test Categories

### 1. Successful Registration (1 test)
- Valid registration with all required fields

### 2. Password Validation (5 tests)
- Passwords don't match
- Password too short (< 8 characters)
- Password missing digit
- Password missing uppercase
- Password missing special character

### 3. Email Validation (4 tests)
- Invalid email format
- Empty email
- Duplicate email (already registered)
- Very long email

### 4. Required Fields Validation (6 tests)
- Missing firstName
- Missing lastName
- Missing birthday
- Missing telephone
- Missing password
- Missing confirmPassword

### 5. Field Format Validation (4 tests)
- firstName too short
- lastName too short
- Invalid telephone format
- Future birthday (invalid)

### 6. Edge Cases (3 tests)
- Empty request body
- Null values
- Very long field values

### 7. Security Tests (2 tests)
- SQL injection attempt in email
- XSS attempt in name fields

**Total: 25 new registration tests**

---

## Test Scenarios

### ✅ Success Case
```json
{
  "firstName": "John",
  "middleName": "William",
  "lastName": "Doe",
  "birthday": "1990-01-15",
  "email": "john.doe@example.com",
  "telephone": "1234567890",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!"
}
```

**Expected:**
- HTTP 201 Created
- Response contains: id, email, status (PENDING), message

---

### ❌ Failure Cases

#### Passwords Don't Match
```json
{
  "password": "SecurePass123!",
  "confirmPassword": "DifferentPass456!"
}
```
**Expected:** 400 Bad Request, error on confirmPassword field

#### Invalid Email
```json
{
  "email": "not-an-email"
}
```
**Expected:** 400 Bad Request, validation error for email

#### Duplicate Email
Register twice with same email
**Expected:** 400 Bad Request, "Email 'x' is already registered"

#### Missing Required Field
```json
{
  // firstName missing
  "lastName": "Doe",
  ...
}
```
**Expected:** 400 Bad Request, "First name is required"

---

## Integration with Existing Tests

Current collection structure:
1. Authentication (11 tests)
2. Admin Endpoints (5 tests)
3. User Endpoints (4 tests)
4. Public Endpoints (2 tests)
5. Security & Edge Cases (6 tests)

**New structure:**
1. **Authentication** (11 tests) - Existing
2. **User Registration** (25 tests) - NEW
3. Admin Endpoints (5 tests)
4. User Endpoints (4 tests)
5. Public Endpoints (2 tests)
6. Security & Edge Cases (6 tests)

**Total: 53 tests → 78 tests (+ 47% increase)**
