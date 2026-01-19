#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}======================================${NC}"
echo -e "${YELLOW}Task Tracker Gamify - Application Test${NC}"
echo -e "${YELLOW}======================================${NC}"
echo ""

# Navigate to project directory
cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify

# Check if PostgreSQL is running
echo -e "${YELLOW}1. Checking PostgreSQL database...${NC}"
if nc -z localhost 5432 2>/dev/null; then
    echo -e "${GREEN}✓ PostgreSQL is running on port 5432${NC}"
else
    echo -e "${RED}✗ PostgreSQL is not accessible on port 5432${NC}"
    echo -e "${YELLOW}  Please ensure Docker container is running${NC}"
fi
echo ""

# Build the application
echo -e "${YELLOW}2. Building the application...${NC}"
./mvnw clean package -DskipTests -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi
echo ""

# Start the application
echo -e "${YELLOW}3. Starting Spring Boot application...${NC}"
java -jar target/task-tracker-gamify-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
APP_PID=$!
echo -e "${GREEN}✓ Application started with PID: $APP_PID${NC}"
echo ""

# Wait for application to start
echo -e "${YELLOW}4. Waiting for application to start (30 seconds)...${NC}"
for i in {1..30}; do
    if curl -s http://localhost:8080/api/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Application is ready!${NC}"
        break
    fi
    echo -n "."
    sleep 1
done
echo ""
echo ""

# Test health endpoint
echo -e "${YELLOW}5. Testing Health Endpoint...${NC}"
echo -e "${YELLOW}   GET http://localhost:8080/api/actuator/health${NC}"
HEALTH_RESPONSE=$(curl -s http://localhost:8080/api/actuator/health)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Health endpoint is accessible${NC}"
    echo ""
    echo -e "${YELLOW}Response:${NC}"
    echo "$HEALTH_RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$HEALTH_RESPONSE"
else
    echo -e "${RED}✗ Health endpoint failed${NC}"
    echo ""
    echo -e "${YELLOW}Application logs:${NC}"
    tail -50 app.log
fi
echo ""

# Additional actuator endpoints
echo -e "${YELLOW}6. Testing other Actuator endpoints...${NC}"
curl -s http://localhost:8080/api/actuator/info > /dev/null 2>&1 && echo -e "${GREEN}✓ /actuator/info is accessible${NC}" || echo -e "${RED}✗ /actuator/info failed${NC}"
curl -s http://localhost:8080/api/actuator/metrics > /dev/null 2>&1 && echo -e "${GREEN}✓ /actuator/metrics is accessible${NC}" || echo -e "${RED}✗ /actuator/metrics failed${NC}"
echo ""

# Stop the application
echo -e "${YELLOW}7. Stopping the application...${NC}"
kill $APP_PID 2>/dev/null
echo -e "${GREEN}✓ Application stopped${NC}"
echo ""

echo -e "${YELLOW}======================================${NC}"
echo -e "${GREEN}Test completed!${NC}"
echo -e "${YELLOW}======================================${NC}"
