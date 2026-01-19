#!/bin/bash

echo "========================================="
echo "Testing Task Tracker Gamify Application"
echo "========================================="
echo ""

cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify

# Kill any existing processes
echo "1. Cleaning up any existing processes..."
pkill -f "task-tracker-gamify" 2>/dev/null
sleep 2

# Build the application
echo "2. Building application..."
./mvnw clean package -DskipTests -q
if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi
echo "✅ Build successful"
echo ""

# Start the application
echo "3. Starting Spring Boot application..."
java -jar target/task-tracker-gamify-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
APP_PID=$!
echo "   Application started with PID: $APP_PID"
echo ""

# Wait for startup
echo "4. Waiting for application to start (30 seconds)..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/actuator/health > /dev/null 2>&1; then
        echo "✅ Application started successfully!"
        break
    fi
    if ! ps -p $APP_PID > /dev/null 2>&1; then
        echo "❌ Application process died. Checking logs..."
        tail -30 app.log
        exit 1
    fi
    echo -n "."
    sleep 1
done
echo ""
echo ""

# Test health endpoint
echo "5. Testing health endpoint..."
echo "   URL: http://localhost:8080/api/actuator/health"
RESPONSE=$(curl -s http://localhost:8080/api/actuator/health)
if [ $? -eq 0 ]; then
    echo "✅ Health endpoint responded"
    echo ""
    echo "Response:"
    echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
else
    echo "❌ Health endpoint failed"
    echo "Application logs:"
    tail -50 app.log
fi
echo ""

# Stop application
echo "6. Stopping application (PID: $APP_PID)..."
kill $APP_PID 2>/dev/null
sleep 2
echo "✅ Application stopped"
echo ""
echo "========================================="
echo "Test Complete!"
echo "========================================="
