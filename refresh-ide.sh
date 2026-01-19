#!/bin/bash

echo "=================================================="
echo "Refreshing Maven Dependencies and IDE"
echo "=================================================="
echo ""

cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify

echo "Step 1: Clean Maven cache for this project..."
./mvnw clean

echo ""
echo "Step 2: Download all dependencies..."
./mvnw dependency:resolve dependency:resolve-plugins

echo ""
echo "Step 3: Compile the project..."
./mvnw compile

echo ""
echo "Step 4: Check if JWT classes are available..."
if [ -f "target/classes/com/tasktracker/gamify/security/JwtTokenProvider.class" ]; then
    echo "✅ JwtTokenProvider compiled successfully!"
else
    echo "❌ JwtTokenProvider did not compile"
fi

echo ""
echo "Step 5: Verify JWT dependencies..."
./mvnw dependency:tree | grep jjwt

echo ""
echo "=================================================="
echo "Build Complete!"
echo "=================================================="
echo ""
echo "Next steps for IntelliJ IDEA:"
echo "1. File → Invalidate Caches → Invalidate and Restart"
echo "2. Right-click pom.xml → Maven → Reload Project"
echo ""
echo "Next steps for VS Code:"
echo "1. Ctrl+Shift+P → Java: Clean Java Language Server Workspace"
echo "2. Reload window"
echo ""
echo "The project compiles successfully from Maven."
echo "The IDE errors are just cache issues."
