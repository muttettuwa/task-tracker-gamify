# Task Tracker Gamify - Setup & Configuration Guide

Complete guide for setting up, configuring, and running the Task Tracker Gamify backend service.

---

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Database Setup](#database-setup)
3. [Application Configuration](#application-configuration)
4. [Running the Application](#running-the-application)
5. [Testing](#testing)
6. [Troubleshooting](#troubleshooting)
7. [Production Checklist](#production-checklist)

---

## Prerequisites

### Required Software
- **Java:** JDK 17 or higher
- **Maven:** 3.6+ (or use included wrapper `./mvnw`)
- **PostgreSQL:** 15+ (Docker recommended)
- **IDE:** IntelliJ IDEA or Eclipse (with Lombok plugin)

### Recommended Tools
- Docker Desktop (for PostgreSQL)
- Postman or curl (for API testing)
- DBeaver or pgAdmin (for database management)

---

## Database Setup

### Option 1: Docker (Recommended)

```bash
# Start PostgreSQL in Docker
docker run --name taskdb-postgres \
  -e POSTGRES_DB=taskdb \
  -e POSTGRES_USER=task_user \
  -e POSTGRES_PASSWORD=task_secret \
  -p 5432:5432 \
  -d postgres:15

# Verify it's running
docker ps | grep taskdb-postgres
```

### Option 2: Local PostgreSQL

```sql
-- Connect as postgres superuser
psql -U postgres

-- Create database and user
CREATE DATABASE taskdb;
CREATE USER task_user WITH PASSWORD 'task_secret';
GRANT ALL PRIVILEGES ON DATABASE taskdb TO task_user;
```

### Verify Database Connection

```bash
psql -h localhost -p 5432 -U task_user -d taskdb
# Password: task_secret

# You should see:
# taskdb=>
```

---

## Application Configuration

### application.yml Structure

```yaml
spring:
  application:
    name: task-tracker-gamify

  # Database Configuration
  datasource:
    url: jdbc:postgresql://localhost:5432/taskdb
    username: task_user
    password: task_secret
    driver-class-name: org.postgresql.Driver
    hikari:
      connection-timeout: 20000
      maximum-pool-size: 10
      minimum-idle: 5

  # JPA & Hibernate
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate  # Safe with Liquibase
    show-sql: true

  # Liquibase
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.xml
    default-schema: public

# Actuator Endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  health:
    db:
      enabled: true
    mail:
      enabled: false  # Disabled until configured

# Server
server:
  port: 8080
  servlet:
    context-path: /api
```

### Environment-Specific Configuration

Create `application-dev.yml`, `application-prod.yml` for different environments:

```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSWORD}
  jpa:
    show-sql: false
logging:
  level:
    root: WARN
    com.tasktracker.gamify: INFO
```

Run with profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## Running the Application

### Method 1: Maven Wrapper (Recommended)

```bash
# Clean and start
./mvnw clean spring-boot:run

# Skip tests
./mvnw spring-boot:run -DskipTests

# With specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Method 2: Build JAR and Run

```bash
# Build
./mvnw clean package -DskipTests

# Run JAR
java -jar target/task-tracker-gamify-0.0.1-SNAPSHOT.jar

# With profile
java -jar -Dspring.profiles.active=prod target/task-tracker-gamify-0.0.1-SNAPSHOT.jar
```

### Method 3: Use Helper Scripts

```bash
# Smart startup (kills port 8080, builds, runs)
./start-app.sh

# Test all endpoints
./test-endpoints.sh

# Quick integration test
./quick-test.sh
```

### Expected Startup Logs

```
Starting data initialization...
Creating default organization...
Default organization: Organization{id=1, name='Default Organization', code='DEFAULT_ORG'}
System roles verified: SUPER_ADMIN, SUPERVISOR, USER, GUEST
Creating default Super Admin user...
Default Super Admin user: Super Admin (admin@tasktracker.com)
Creating sample regular user...
Sample user: John A. Doe (user@tasktracker.com)
=================================================
Data Initialization Complete!
=================================================
Organizations: 1
System Roles: 4
Users: 2
User Role Assignments: 2
=================================================
Started TaskTrackerGamifyApplication in 10.033 seconds
Tomcat started on port 8080 (http) with context path '/api'
```

---

## Testing

### Health Check

```bash
# Basic health check
curl http://localhost:8080/api/actuator/health

# Expected response:
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

### Other Actuator Endpoints

```bash
# Application info
curl http://localhost:8080/api/actuator/info

# Metrics
curl http://localhost:8080/api/actuator/metrics

# Prometheus metrics
curl http://localhost:8080/api/actuator/prometheus
```

### Database Verification

```sql
-- Connect to database
psql -h localhost -p 5432 -U task_user -d taskdb

-- Check tables exist
\dt

-- Verify data
SELECT * FROM organizations;
SELECT * FROM system_roles;
SELECT id, first_name, last_name, email, status FROM user_info;

-- Check role assignments
SELECT 
    ui.email,
    o.name as organization,
    sr.code as role,
    ur.active
FROM user_roles ur
JOIN user_info ui ON ur.user_info_id = ui.id
JOIN organizations o ON ur.organization_id = o.id
JOIN system_roles sr ON ur.system_role_id = sr.id
WHERE ur.logically_deleted = false;
```

---

## Troubleshooting

### Issue: Port 8080 Already in Use

**Error:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solutions:**

1. **Kill the process:**
```bash
# Find process
lsof -i :8080

# Kill it
kill -9 <PID>
```

2. **Use the startup script:**
```bash
./start-app.sh  # Automatically handles port cleanup
```

3. **Change the port:**
```yaml
# application.yml
server:
  port: 8081  # Use different port
```

### Issue: Database Connection Failed

**Error:**
```
Failed to obtain JDBC Connection
```

**Checks:**

1. **Verify PostgreSQL is running:**
```bash
docker ps | grep postgres
# or
lsof -i :5432
```

2. **Test connection manually:**
```bash
psql -h localhost -p 5432 -U task_user -d taskdb
```

3. **Check credentials in application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskdb
    username: task_user
    password: task_secret
```

### Issue: Liquibase Migration Failed

**Error:**
```
Error parsing line X column Y of db/changelog/...
```

**Solutions:**

1. **Verify XML syntax:**
   - Check for unclosed tags
   - Verify schema location
   - Ensure proper structure

2. **Clean and rebuild:**
```bash
./mvnw clean compile
```

3. **Check Liquibase tables:**
```sql
SELECT * FROM databasechangelog;
SELECT * FROM databasechangeloglock;
```

### Issue: Lombok Not Working in IDE

**Symptoms:**
- Cannot resolve `@Getter`, `@Setter`
- Methods not recognized

**Solutions:**

**IntelliJ IDEA:**
1. Settings → Plugins → Search "Lombok" → Install
2. Settings → Build, Execution, Deployment → Compiler → Annotation Processors
3. Check "Enable annotation processing"
4. Restart IDE

**Eclipse:**
1. Download lombok.jar
2. Run: `java -jar lombok.jar`
3. Select Eclipse installation
4. Restart Eclipse

**Note:** Code compiles fine even without IDE plugin!

### Issue: Application Starts but Health Returns 503

**Cause:** Database health check failing

**Solutions:**

1. **Verify database is accessible**
2. **Check Liquibase ran successfully:**
```sql
SELECT * FROM databasechangelog ORDER BY dateexecuted DESC LIMIT 5;
```

3. **Check application logs for errors**

---

## IDE Setup

### IntelliJ IDEA

1. **Import Project:**
   - File → Open → Select `pom.xml`
   - Import as Maven project

2. **Install Lombok Plugin:**
   - Settings → Plugins → Search "Lombok"
   - Install and restart

3. **Enable Annotation Processing:**
   - Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Set JDK:**
   - File → Project Structure → Project
   - Set SDK to Java 17

### Eclipse

1. **Import Project:**
   - File → Import → Maven → Existing Maven Projects
   - Select project directory

2. **Install Lombok:**
   - Run: `java -jar lombok.jar`
   - Select Eclipse installation

3. **Configure JDK:**
   - Right-click project → Properties → Java Build Path
   - Set JRE to Java 17

---

## Production Checklist

### Before Deploying to Production

#### Security
- [ ] Implement BCryptPasswordEncoder for password hashing
- [ ] Remove placeholder password hashes
- [ ] Enable CSRF protection in SecurityConfig
- [ ] Configure proper authentication/authorization
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS/TLS
- [ ] Secure actuator endpoints (remove permitAll)
- [ ] Review and audit security settings

#### Configuration
- [ ] Set `spring.jpa.show-sql: false`
- [ ] Set logging levels to INFO or WARN
- [ ] Configure proper error handling
- [ ] Set `spring.profiles.active=prod`
- [ ] Use production database credentials
- [ ] Configure connection pool for production load
- [ ] Set up database backup strategy

#### Mail
- [ ] Configure real SMTP server credentials
- [ ] Enable mail health check
- [ ] Test email sending functionality

#### Monitoring
- [ ] Set up monitoring and alerting
- [ ] Configure log aggregation
- [ ] Enable application metrics
- [ ] Set up health check monitoring

#### Infrastructure
- [ ] Set up reverse proxy (Nginx/Apache)
- [ ] Configure load balancer (if needed)
- [ ] Set up CI/CD pipeline
- [ ] Configure auto-scaling rules
- [ ] Set up database replication/clustering

#### Testing
- [ ] Run all unit tests
- [ ] Run integration tests
- [ ] Perform load testing
- [ ] Security testing/penetration testing
- [ ] Verify backup and restore procedures

---

## Configuration Reference

### Database Connection Pool (HikariCP)

```yaml
spring:
  datasource:
    hikari:
      connection-timeout: 20000      # 20 seconds
      maximum-pool-size: 10          # Max 10 connections
      minimum-idle: 5                # Keep 5 idle
      idle-timeout: 300000           # 5 minutes
      max-lifetime: 1200000          # 20 minutes
      leak-detection-threshold: 60000 # 1 minute
```

### Logging Levels

```yaml
logging:
  level:
    root: INFO
    com.tasktracker.gamify: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.springframework.security: DEBUG
    liquibase: INFO
```

### Actuator Configuration

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized  # or 'always' for debugging
      probes:
        enabled: true
```

---

## Environment Variables

### Recommended for Production

```bash
# Database
export DATABASE_URL=jdbc:postgresql://prod-db-host:5432/taskdb
export DATABASE_USER=prod_user
export DATABASE_PASSWORD=secure_password

# Application
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=prod

# Mail
export MAIL_HOST=smtp.gmail.com
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password

# Run application
java -jar target/task-tracker-gamify-0.0.1-SNAPSHOT.jar
```

### Use in application.yml

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/taskdb}
    username: ${DATABASE_USER:task_user}
    password: ${DATABASE_PASSWORD:task_secret}
```

---

## Performance Tuning

### JVM Options

```bash
java -Xms512m -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar target/task-tracker-gamify-0.0.1-SNAPSHOT.jar
```

### Database Optimization

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_user_email_lower ON user_info (LOWER(email));
CREATE INDEX idx_user_organization_status ON user_info (organization_id, status);

-- Analyze tables
ANALYZE organizations;
ANALYZE user_info;
ANALYZE user_roles;
```

---

## Quick Reference

### Start Application
```bash
./mvnw spring-boot:run
```

### Test Health
```bash
curl http://localhost:8080/api/actuator/health
```

### Check Logs
```bash
tail -f logs/spring.log
```

### Database Console
```bash
psql -h localhost -p 5432 -U task_user -d taskdb
```

### Useful Commands
```bash
# Clean build
./mvnw clean package -DskipTests

# Run tests only
./mvnw test

# Update dependencies
./mvnw versions:display-dependency-updates

# Check for CVEs
./mvnw dependency-check:check
```

---

**For additional help, see README.md and DEVELOPMENT-NOTES.md**
