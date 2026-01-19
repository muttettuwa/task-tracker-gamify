#!/bin/bash

echo "=========================================="
echo "Domain Model - Startup Test"
echo "=========================================="
echo ""

cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify

# Kill any existing processes
echo "1. Cleaning up existing processes..."
pkill -9 -f "task-tracker-gamify" 2>/dev/null
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
echo "3. Starting application with domain model..."
echo "   Watch for data initialization logs..."
echo ""
echo "=========================================="
echo ""

# Start and capture important logs
./mvnw spring-boot:run 2>&1 | grep -E "(Starting data initialization|Creating default|role assigned|Data Initialization Complete|Organizations:|System Roles:|Users:|User Role Assignments:|Default Credentials:|Started TaskTrackerGamifyApplication)" &

APP_PID=$!
echo "Application started with PID: $APP_PID"
echo ""
echo "Press Ctrl+C to stop the application"
echo ""

# Wait for the application
wait $APP_PID
