#!/bin/bash

echo "=================================================="
echo "JWT Authentication Troubleshooting"
echo "=================================================="
echo ""

# Check if application is running
if ! curl -s http://localhost:8080/api/actuator/health > /dev/null; then
    echo "❌ Application is not running!"
    echo "Start it with: ./mvnw spring-boot:run"
    exit 1
fi

echo "✅ Application is running"
echo ""

# Test 1: Check database connection
echo "📝 Step 1: Checking database users..."
psql -h localhost -p 5432 -U task_user -d taskdb -c \
  "SELECT id, email, LEFT(password_hash, 20) as pwd_prefix, status FROM user_info WHERE email IN ('admin@tasktracker.com', 'user@tasktracker.com');" 2>/dev/null

if [ $? -ne 0 ]; then
    echo "⚠️  Cannot connect to database directly"
    echo "   This is OK if passwords were set during startup"
fi
echo ""

# Test 2: Try to login
echo "📝 Step 2: Testing login endpoint..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tasktracker.com","password":"admin123"}')

HTTP_CODE=$(echo "$RESPONSE" | grep -o 'HTTP_CODE:[0-9]*' | cut -d':' -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')

echo "HTTP Status: $HTTP_CODE"
echo "Response: $BODY"
echo ""

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Login successful!"
    echo ""
    echo "Token received:"
    echo "$BODY" | jq -r '.token' 2>/dev/null | cut -c1-50
    echo "..."
    exit 0
elif [ "$HTTP_CODE" = "401" ]; then
    echo "❌ Login failed: Invalid credentials (401)"
    echo ""
    echo "SOLUTION 1: Reset database and restart application"
    echo "  ./mvnw spring-boot:run"
    echo ""
    echo "SOLUTION 2: Manually fix passwords in database"
    echo "  psql -h localhost -p 5432 -U task_user -d taskdb -f fix-passwords.sql"
elif [ "$HTTP_CODE" = "500" ]; then
    echo "❌ Server error (500)"
    echo ""
    echo "Check application logs for the error:"
    echo "  Error during authentication for email: admin@tasktracker.com"
    echo ""
    echo "COMMON CAUSES:"
    echo "1. Password in database is not BCrypt encoded"
    echo "2. User roles not properly assigned"
    echo "3. Organization not found"
    echo ""
    echo "SOLUTIONS:"
    echo ""
    echo "Option A: Delete users and restart application (clean slate)"
    echo "  psql -h localhost -p 5432 -U task_user -d taskdb << EOF"
    echo "  DELETE FROM user_roles WHERE user_info_id IN (SELECT id FROM user_info WHERE email IN ('admin@tasktracker.com', 'user@tasktracker.com'));"
    echo "  DELETE FROM user_info WHERE email IN ('admin@tasktracker.com', 'user@tasktracker.com');"
    echo "  EOF"
    echo "  Then restart: ./mvnw spring-boot:run"
    echo ""
    echo "Option B: Manually update passwords to BCrypt hashes"
    echo "  psql -h localhost -p 5432 -U task_user -d taskdb -f fix-passwords.sql"
    echo ""
else
    echo "❌ Unexpected response code: $HTTP_CODE"
    echo "Response body: $BODY"
fi

echo ""
echo "=================================================="
echo "Need help? Check the application logs"
echo "=================================================="
