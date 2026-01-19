# Task Tracker API - Quick Test Reference

## 🚀 Quick Start

```bash
# Import collection into Postman
File → Import → Task-Tracker-Complete.postman_collection.json

# Create environment variable
base_url = http://localhost:8080/api

# Run collection
Collections → Task Tracker → Run
```

---

## 📊 Test Coverage at a Glance

| Category | Tests | What's Tested |
|----------|-------|---------------|
| 🔐 Authentication | 11 | Login validation, Bean Validation |
| 👑 Admin Access | 5 | SUPER_ADMIN role enforcement |
| 👤 User Access | 4 | USER role & hierarchical access |
| ⚕️ Actuator | 2 | Public health endpoints |
| 🔒 Security | 6 | SQL injection, XSS, input validation |
| **TOTAL** | **28** | **Complete API coverage** |

---

## 🎯 Key Test Scenarios

### ✅ Should SUCCEED (9 tests)
- Admin login with valid credentials → 200 + JWT
- User login with valid credentials → 200 + JWT
- Admin accessing /admin/test → 200
- Admin accessing /admin/dashboard → 200
- User accessing /user/test → 200
- Admin accessing /user/test → 200 (hierarchical)
- User accessing /user/profile → 200
- /actuator/health → 200 (public)
- /actuator/info → 200 (public)

### ❌ Should FAIL (19 tests)
- Invalid password → 401
- Non-existent user → 401
- Empty email → 400
- Empty password → 400
- Invalid email format → 400
- Missing fields → 400
- User accessing /admin/* → 403
- No token accessing protected endpoints → 403
- Invalid token → 403
- SQL injection → 400/401
- XSS attempt → 400
- Other edge cases → 400

---

## 🔑 Default Test Accounts

```
Admin:
  Email: admin@tasktracker.com
  Password: admin123
  Role: SUPER_ADMIN

User:
  Email: user@tasktracker.com
  Password: user123
  Role: USER
```

---

## 📝 Bean Validation Rules

```java
Email:
  @NotBlank - Cannot be empty/whitespace
  @Email    - Must be valid email format

Password:
  @NotBlank - Cannot be empty/whitespace
```

---

## 🛡️ Authorization Matrix

```
Endpoint              | Public | USER | SUPER_ADMIN
----------------------|--------|------|-------------
/auth/login           |   ✅   |  ✅  |     ✅
/actuator/health      |   ✅   |  ✅  |     ✅
/user/test            |   ❌   |  ✅  |     ✅
/user/profile         |   ❌   |  ✅  |     ✅
/admin/test           |   ❌   |  ❌  |     ✅
/admin/dashboard      |   ❌   |  ❌  |     ✅
```

---

## 🧪 Running Tests

### Option 1: Postman Collection Runner
1. Click collection → **Run**
2. Select all 28 requests
3. Click **Run Task Tracker**
4. View results in runner

### Option 2: Individual Tests
1. Open request
2. Click **Send**
3. View **Test Results** tab

### Option 3: Newman CLI
```bash
npm install -g newman
newman run Task-Tracker-Complete.postman_collection.json
```

---

## ✅ Expected Results

**Total:** 28 tests
**Pass:** All 28 (mix of success and expected failures)

**Breakdown:**
- ✅ 9 successful operations (200)
- ✅ 10 validation errors (400) - Expected!
- ✅ 2 authentication failures (401) - Expected!
- ✅ 7 authorization denials (403) - Expected!

**All results are expected behaviors!**

---

## 📂 Collection Structure

```
Task Tracker - Complete API Testing
├── 🔐 Authentication (11)
│   ├── Success scenarios (2)
│   └── Validation failures (9)
├── 👑 Admin Endpoints (5)
│   ├── Authorized (2)
│   └── Unauthorized (3)
├── 👤 User Endpoints (4)
│   ├── Authorized (3)
│   └── Unauthorized (1)
├── ⚕️ Actuator Endpoints (2)
│   └── Public access (2)
└── 🔒 Edge Cases & Security (6)
    └── Attack prevention (6)
```

---

## 💡 Pro Tips

1. **Run Authentication first** - populates tokens
2. **Use environment variables** - automatic token management
3. **Check test results tab** - see all assertions
4. **Review console** - detailed logs
5. **Save responses** - for documentation

---

## 🔍 Quick Debug

**If tests fail unexpectedly:**

1. **Check application is running**
   ```bash
   curl http://localhost:8080/api/actuator/health
   ```

2. **Verify base_url in environment**
   ```
   http://localhost:8080/api
   ```

3. **Clear environment tokens**
   - Delete saved tokens
   - Re-run Authentication folder

4. **Check application logs**
   - Look for errors
   - Verify database connection

---

## 📖 Documentation Files

- **Collection:** `Task-Tracker-Complete.postman_collection.json`
- **Full Guide:** `API-TESTING-GUIDE.md`
- **JWT Docs:** `JWT-AUTHENTICATION-GUIDE.md`
- **Setup:** `SETUP-GUIDE.md`

---

## 🎯 Common Test Scenarios

### Test Invalid Email Format
```json
{
  "email": "not-an-email",
  "password": "admin123"
}
// Expected: 400 Bad Request
```

### Test Empty Password
```json
{
  "email": "admin@tasktracker.com",
  "password": ""
}
// Expected: 400 Bad Request
```

### Test Valid Login
```json
{
  "email": "admin@tasktracker.com",
  "password": "admin123"
}
// Expected: 200 OK + JWT token
```

### Test Unauthorized Access
```http
GET /admin/test
Authorization: Bearer {{user_token}}
// Expected: 403 Forbidden
```

---

## ✅ Validation Checklist

- [ ] Import collection
- [ ] Set base_url environment variable
- [ ] Start application
- [ ] Run Authentication folder
- [ ] Verify tokens saved
- [ ] Run Admin Endpoints folder
- [ ] Run User Endpoints folder
- [ ] Run Actuator folder
- [ ] Run Security folder
- [ ] Review all test results
- [ ] All 28 tests show expected behavior

---

**Import the collection and start testing!** 🚀

**File:** `Task-Tracker-Complete.postman_collection.json`
**Tests:** 28 comprehensive scenarios
**Coverage:** 100% of current API endpoints

---

**Need help?** See `API-TESTING-GUIDE.md` for detailed documentation!
