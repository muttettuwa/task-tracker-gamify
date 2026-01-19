#!/bin/bash

# JWT Authentication Test Script
# Tests login, token generation, and role-based access control

echo "=================================================="
echo "JWT Authentication & Authorization Test"
echo "=================================================="
echo ""

BASE_URL="http://localhost:8080/api"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Login as Super Admin
echo "📝 Test 1: Login as Super Admin"
echo "POST ${BASE_URL}/auth/login"
ADMIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tasktracker.com","password":"admin123"}')

ADMIN_TOKEN=$(echo $ADMIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -n "$ADMIN_TOKEN" ]; then
    echo -e "${GREEN}✓ Admin login successful${NC}"
    echo "Token: ${ADMIN_TOKEN:0:30}..."
    echo "Response: $ADMIN_RESPONSE" | jq '.' 2>/dev/null || echo "$ADMIN_RESPONSE"
else
    echo -e "${RED}✗ Admin login failed${NC}"
    echo "Response: $ADMIN_RESPONSE"
fi
echo ""

# Test 2: Login as Regular User
echo "📝 Test 2: Login as Regular User"
echo "POST ${BASE_URL}/auth/login"
USER_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"user@tasktracker.com","password":"user123"}')

USER_TOKEN=$(echo $USER_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -n "$USER_TOKEN" ]; then
    echo -e "${GREEN}✓ User login successful${NC}"
    echo "Token: ${USER_TOKEN:0:30}..."
    echo "Response: $USER_RESPONSE" | jq '.' 2>/dev/null || echo "$USER_RESPONSE"
else
    echo -e "${RED}✗ User login failed${NC}"
    echo "Response: $USER_RESPONSE"
fi
echo ""

# Test 3: Invalid credentials
echo "📝 Test 3: Login with invalid credentials"
echo "POST ${BASE_URL}/auth/login"
INVALID_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tasktracker.com","password":"wrongpassword"}')

HTTP_CODE=$(echo "$INVALID_RESPONSE" | grep -o 'HTTP_CODE:[0-9]*' | cut -d':' -f2)

if [ "$HTTP_CODE" = "401" ]; then
    echo -e "${GREEN}✓ Correctly returned 401 Unauthorized${NC}"
else
    echo -e "${RED}✗ Expected 401, got $HTTP_CODE${NC}"
fi
echo ""

# Test 4: Admin accessing admin endpoint
echo "📝 Test 4: Admin accessing /admin/test (SUPER_ADMIN only)"
echo "GET ${BASE_URL}/admin/test"
if [ -n "$ADMIN_TOKEN" ]; then
    ADMIN_TEST=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BASE_URL}/admin/test" \
      -H "Authorization: Bearer $ADMIN_TOKEN")

    HTTP_CODE=$(echo "$ADMIN_TEST" | grep -o 'HTTP_CODE:[0-9]*' | cut -d':' -f2)

    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✓ Admin successfully accessed admin endpoint${NC}"
        echo "$ADMIN_TEST" | sed '/HTTP_CODE/d' | jq '.' 2>/dev/null || echo "$ADMIN_TEST"
    else
        echo -e "${RED}✗ Expected 200, got $HTTP_CODE${NC}"
    fi
else
    echo -e "${YELLOW}⊘ Skipped - no admin token${NC}"
fi
echo ""

# Test 5: Regular user trying to access admin endpoint
echo "📝 Test 5: Regular user accessing /admin/test (should be denied)"
echo "GET ${BASE_URL}/admin/test"
if [ -n "$USER_TOKEN" ]; then
    USER_ADMIN_TEST=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BASE_URL}/admin/test" \
      -H "Authorization: Bearer $USER_TOKEN")

    HTTP_CODE=$(echo "$USER_ADMIN_TEST" | grep -o 'HTTP_CODE:[0-9]*' | cut -d':' -f2)

    if [ "$HTTP_CODE" = "403" ]; then
        echo -e "${GREEN}✓ Correctly denied access (403 Forbidden)${NC}"
    else
        echo -e "${RED}✗ Expected 403, got $HTTP_CODE${NC}"
    fi
else
    echo -e "${YELLOW}⊘ Skipped - no user token${NC}"
fi
echo ""

# Test 6: User accessing user endpoint
echo "📝 Test 6: Regular user accessing /user/test"
echo "GET ${BASE_URL}/user/test"
if [ -n "$USER_TOKEN" ]; then
    USER_TEST=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BASE_URL}/user/test" \
      -H "Authorization: Bearer $USER_TOKEN")

    HTTP_CODE=$(echo "$USER_TEST" | grep -o 'HTTP_CODE:[0-9]*' | cut -d':' -f2)

    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✓ User successfully accessed user endpoint${NC}"
        echo "$USER_TEST" | sed '/HTTP_CODE/d' | jq '.' 2>/dev/null || echo "$USER_TEST"
    else
        echo -e "${RED}✗ Expected 200, got $HTTP_CODE${NC}"
    fi
else
    echo -e "${YELLOW}⊘ Skipped - no user token${NC}"
fi
echo ""

# Test 7: Admin accessing user endpoint
echo "📝 Test 7: Admin accessing /user/test (should also work)"
echo "GET ${BASE_URL}/user/test"
if [ -n "$ADMIN_TOKEN" ]; then
    ADMIN_USER_TEST=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BASE_URL}/user/test" \
      -H "Authorization: Bearer $ADMIN_TOKEN")

    HTTP_CODE=$(echo "$ADMIN_USER_TEST" | grep -o 'HTTP_CODE:[0-9]*' | cut -d':' -f2)

    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✓ Admin successfully accessed user endpoint${NC}"
        echo "$ADMIN_USER_TEST" | sed '/HTTP_CODE/d' | jq '.' 2>/dev/null || echo "$ADMIN_USER_TEST"
    else
        echo -e "${RED}✗ Expected 200, got $HTTP_CODE${NC}"
    fi
else
    echo -e "${YELLOW}⊘ Skipped - no admin token${NC}"
fi
echo ""

# Test 8: No token (unauthorized)
echo "📝 Test 8: Accessing protected endpoint without token"
echo "GET ${BASE_URL}/user/test"
NO_TOKEN=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "${BASE_URL}/user/test")

HTTP_CODE=$(echo "$NO_TOKEN" | grep -o 'HTTP_CODE:[0-9]*' | cut -d':' -f2)

if [ "$HTTP_CODE" = "403" ]; then
    echo -e "${GREEN}✓ Correctly denied access without token (403)${NC}"
else
    echo -e "${YELLOW}⊙ Got $HTTP_CODE (403 or 401 expected)${NC}"
fi
echo ""

echo "=================================================="
echo "Test Summary"
echo "=================================================="
echo -e "${GREEN}✓${NC} = Passed"
echo -e "${RED}✗${NC} = Failed"
echo -e "${YELLOW}⊘${NC} = Skipped"
echo ""
echo "All JWT authentication and authorization tests completed!"
