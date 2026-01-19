# 🎯 Port 8080 Already in Use - SOLUTION

## The Issue

Your application failed to start with this error:
```
Web server failed to start. Port 8080 was already in use.
```

**Good News:** The Liquibase error is fixed! Your configuration is correct. The only problem now is that port 8080 is occupied by another process.

---

## ✅ Quick Solution - Use the Startup Script

I've created a startup script that automatically handles port conflicts:

```bash
cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify
./start-app.sh
```

This script will:
1. ✅ Kill any processes using port 8080
2. ✅ Verify the port is free
3. ✅ Check PostgreSQL connection
4. ✅ Build the application
5. ✅ Start the application

---

## 🔧 Manual Solutions

### Option 1: Kill the Process Using Port 8080

**Step 1 - Find the process:**
```bash
lsof -i :8080
```

**Step 2 - Kill the process:**
```bash
kill -9 <PID>
```

Replace `<PID>` with the process ID from step 1.

**Step 3 - Start your application:**
```bash
./mvnw spring-boot:run
```

### Option 2: Change the Application Port

If you want to use a different port (e.g., 8081), edit `application.yml`:

```yaml
server:
  port: 8081  # Changed from 8080
```

Then access your application at:
- Health: `http://localhost:8081/api/actuator/health`
- Other endpoints: `http://localhost:8081/api/...`

### Option 3: Kill All Java Processes

**⚠️ Warning: This will kill ALL Java processes on your system**

```bash
pkill -9 java
```

Then start your application.

---

## 🧪 Testing the Application

### Step 1: Start the Application

Use one of these methods:

**Method A - Using startup script (Recommended):**
```bash
./start-app.sh
```

**Method B - Using Maven:**
```bash
./mvnw spring-boot:run
```

**Method C - Using JAR:**
```bash
java -jar target/task-tracker-gamify-0.0.1-SNAPSHOT.jar
```

### Step 2: Wait for Startup

You should see these messages in the logs:
```
✅ Liquibase successfully ran
✅ HikariPool-1 - Start completed
✅ Started TaskTrackerGamifyApplication in X.XX seconds
✅ Tomcat started on port(s): 8080 (http)
```

### Step 3: Test the Health Endpoint

**Open a NEW terminal** and run:

```bash
# Option 1 - Use the test script
./test-endpoints.sh

# Option 2 - Manual curl command
curl http://localhost:8080/api/actuator/health

# Option 3 - Use browser
# Open: http://localhost:8080/api/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

---

## 📁 Helper Scripts Created

I've created several helper scripts for you:

### 1. `start-app.sh` - Smart Startup
- Automatically kills processes on port 8080
- Checks PostgreSQL connection
- Builds and starts the application

```bash
./start-app.sh
```

### 2. `test-endpoints.sh` - Test All Endpoints
- Waits for application to start
- Tests health, info, and metrics endpoints
- Pretty prints JSON responses

```bash
# Run this in a separate terminal AFTER starting the app
./test-endpoints.sh
```

### 3. `quick-test.sh` - Full Integration Test
- Builds, starts, tests, and stops the application
- Useful for CI/CD or quick verification

```bash
./quick-test.sh
```

---

## 🔍 Troubleshooting

### Problem: Port still in use after killing process

**Solution:**
```bash
# Find ALL processes on port 8080
sudo lsof -i :8080

# Kill them all
sudo lsof -ti :8080 | xargs kill -9

# Wait a few seconds
sleep 3

# Try starting again
./start-app.sh
```

### Problem: PostgreSQL connection failed

**Check Docker container:**
```bash
# List running containers
docker ps

# Start PostgreSQL container if not running
docker start <container-name>

# Or create a new one
docker run --name taskdb-postgres \
  -e POSTGRES_DB=taskdb \
  -e POSTGRES_USER=task_user \
  -e POSTGRES_PASSWORD=task_secret \
  -p 5432:5432 \
  -d postgres:15
```

### Problem: "Address already in use" on macOS

Sometimes macOS caches the port. Try:
```bash
# Kill any Java processes
pkill -9 java

# Clear the port (may require sudo)
sudo lsof -ti :8080 | xargs sudo kill -9

# Wait a moment
sleep 5

# Start again
./start-app.sh
```

### Problem: Application starts but health endpoint returns 503

This means the database connection failed. Check:

1. **Is PostgreSQL running?**
   ```bash
   docker ps | grep postgres
   ```

2. **Can you connect manually?**
   ```bash
   psql -h localhost -p 5432 -U task_user -d taskdb
   # Password: task_secret
   ```

3. **Check application.yml settings:**
   - URL: `jdbc:postgresql://localhost:5432/taskdb`
   - Username: `task_user`
   - Password: `task_secret`

---

## 🎉 What's Working Now

Based on your logs, I can see:

✅ **Liquibase** - Successfully ran migrations  
✅ **Database Connection** - HikariPool connected to PostgreSQL 15.15  
✅ **JPA/Hibernate** - EntityManagerFactory initialized  
✅ **Actuator** - 3 endpoints exposed  
✅ **Security** - Configured (generated password shown)  

The **ONLY** issue was port 8080 being in use.

---

## 🚀 Quick Start Guide

**Complete these steps in order:**

1. **Kill any processes on port 8080:**
   ```bash
   lsof -ti :8080 | xargs kill -9 2>/dev/null
   ```

2. **Start the application:**
   ```bash
   cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify
   ./start-app.sh
   ```

3. **Wait for this message:**
   ```
   Started TaskTrackerGamifyApplication in X.XX seconds
   ```

4. **Open a NEW terminal and test:**
   ```bash
   curl http://localhost:8080/api/actuator/health
   ```

5. **Verify response shows:** `"status": "UP"`

**Done! Your application is running! 🎊**

---

## 📞 Need Help?

If you're still having issues:

1. Check the application logs for specific errors
2. Verify PostgreSQL is running: `docker ps`
3. Make sure port 8080 is free: `lsof -i :8080`
4. Try changing the port in `application.yml` to 8081

---

**Everything is configured correctly. Just need to free up port 8080 and start the app!** 🚀
