#!/bin/bash

echo "=========================================="
echo "Task Tracker Gamify - Startup Script"
echo "=========================================="
echo ""

cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify

# Function to kill processes on port 8080
kill_port_8080() {
    echo "🔍 Checking for processes on port 8080..."

    # Try lsof first
    PID=$(lsof -ti :8080)
    if [ ! -z "$PID" ]; then
        echo "Found process $PID using port 8080"
        echo "Killing process..."
        kill -9 $PID
        sleep 2
        echo "✅ Process killed"
        return 0
    fi

    # Also kill any Java processes related to task-tracker
    JAVA_PIDS=$(ps aux | grep -i "task-tracker-gamify" | grep -v grep | awk '{print $2}')
    if [ ! -z "$JAVA_PIDS" ]; then
        echo "Found Java processes: $JAVA_PIDS"
        echo "Killing them..."
        echo "$JAVA_PIDS" | xargs kill -9 2>/dev/null
        sleep 2
        echo "✅ Java processes killed"
        return 0
    fi

    echo "No processes found on port 8080"
}

# Clean up port 8080
kill_port_8080
echo ""

# Verify port is free
echo "🔍 Verifying port 8080 is available..."
if lsof -i :8080 >/dev/null 2>&1; then
    echo "❌ ERROR: Port 8080 is still in use!"
    echo "Please manually kill the process or change the application port."
    echo ""
    echo "To find the process:"
    echo "  lsof -i :8080"
    echo ""
    echo "To kill it:"
    echo "  kill -9 <PID>"
    exit 1
fi
echo "✅ Port 8080 is available"
echo ""

# Check PostgreSQL
echo "🔍 Checking PostgreSQL connection..."
if nc -z localhost 5432 2>/dev/null; then
    echo "✅ PostgreSQL is running on port 5432"
else
    echo "⚠️  WARNING: PostgreSQL may not be running on port 5432"
    echo "   Make sure your Docker container is running"
fi
echo ""

# Build the application
echo "🔨 Building application..."
./mvnw clean package -DskipTests -q
if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi
echo "✅ Build successful"
echo ""

# Start the application
echo "🚀 Starting Spring Boot application..."
echo "   This will run in the foreground. Press Ctrl+C to stop."
echo "   The application will be available at: http://localhost:8080/api"
echo "   Health endpoint: http://localhost:8080/api/actuator/health"
echo ""
echo "=========================================="
echo ""

# Start with colored output
exec ./mvnw spring-boot:run
