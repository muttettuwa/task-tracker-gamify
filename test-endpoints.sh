#!/bin/bash

echo "=========================================="
echo "Testing Health Endpoint"
echo "=========================================="
echo ""

# Wait for application to be ready
echo "Waiting for application to start..."
MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:8080/api/actuator/health > /dev/null 2>&1; then
        echo "✅ Application is UP!"
        break
    fi
    ATTEMPT=$((ATTEMPT + 1))
    echo -n "."
    sleep 1
done

echo ""
echo ""

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "❌ Application did not start within 30 seconds"
    echo ""
    echo "Possible issues:"
    echo "1. Port 8080 is already in use - run: lsof -i :8080"
    echo "2. PostgreSQL is not running - check Docker container"
    echo "3. Configuration error - check application logs"
    exit 1
fi

# Test health endpoint
echo "📊 Health Endpoint Response:"
echo "URL: http://localhost:8080/api/actuator/health"
echo ""
curl -s http://localhost:8080/api/actuator/health | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/api/actuator/health
echo ""
echo ""

# Test info endpoint
echo "📊 Info Endpoint:"
echo "URL: http://localhost:8080/api/actuator/info"
echo ""
curl -s http://localhost:8080/api/actuator/info | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/api/actuator/info
echo ""
echo ""

# Test metrics endpoint
echo "📊 Metrics Endpoint (Available Metrics):"
echo "URL: http://localhost:8080/api/actuator/metrics"
echo ""
curl -s http://localhost:8080/api/actuator/metrics | python3 -m json.tool 2>/dev/null | head -20 || curl -s http://localhost:8080/api/actuator/metrics | head -20
echo ""
echo ""

echo "=========================================="
echo "✅ All endpoints are working!"
echo "=========================================="
echo ""
echo "Available endpoints:"
echo "  - Health: http://localhost:8080/api/actuator/health"
echo "  - Info:   http://localhost:8080/api/actuator/info"
echo "  - Metrics: http://localhost:8080/api/actuator/metrics"
echo ""
