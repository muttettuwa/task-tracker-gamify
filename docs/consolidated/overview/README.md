# Task Tracker Gamify - Backend Service

A Spring Boot-based task tracking and gamification backend service with user management, role-based access control, and PostgreSQL database.

## 🎯 Quick Start

### Prerequisites
- Java 17+
- PostgreSQL 15+ (running in Docker)
- Maven 3.6+

### Start the Application
```bash
./mvnw spring-boot:run
```

### Test Health Endpoint
```bash
curl http://localhost:8080/api/actuator/health
```

Expected response: `{"status":"UP"}`

---

## 📦 Project Overview

### Technology Stack
- **Framework:** Spring Boot 3.5.9
- **Database:** PostgreSQL 15.15
- **ORM:** JPA/Hibernate
- **Migration:** Liquibase
- **Security:** Spring Security
- **Validation:** Bean Validation
- **Utilities:** Lombok, MapStruct
- **Mail:** Spring Mail (optional)
- **Monitoring:** Spring Actuator

### Application Configuration
- **Server Port:** 8080
- **Context Path:** `/api`
- **Database:** PostgreSQL (localhost:5432/taskdb)
- **Database User:** task_user
- **Database Password:** task_secret

---

## 🗄️ Database Schema

### Tables

#### organizations (1 default row)
- `id` (PK, BIGSERIAL)
- `name` (VARCHAR 255, NOT NULL)
- `code` (VARCHAR 50, UNIQUE, NOT NULL)
- `is_active` (BOOLEAN, DEFAULT true)
- `created_at`, `updated_at` (TIMESTAMP)

#### system_roles (4 default rows)
- `id` (PK, BIGSERIAL)
- `code` (VARCHAR 50, UNIQUE) - SUPER_ADMIN, SUPERVISOR, USER, GUEST
- `description` (VARCHAR 500)

#### user_info
- `id` (PK, BIGSERIAL)
- `organization_id` (FK → organizations)
- `first_name`, `middle_name`, `last_name`
- `birthday`, `email` (UNIQUE), `telephone`, `password_hash`
- `status` (ENUM: PENDING, APPROVED, REJECTED, LOCKED)
- `registered_date`, `system_inserted_ts`, `system_updated_ts`
- `logically_deleted` (BOOLEAN, soft delete flag)

#### user_roles
- `id` (PK, BIGSERIAL)
- `user_info_id`, `organization_id`, `system_role_id` (FKs)
- `active`, `logically_deleted` (BOOLEAN)
- `system_inserted_ts`, `system_updated_ts`
- UNIQUE constraint on (user_info_id, organization_id, system_role_id)

---

## 👥 Default Data

### Organizations
- **Default Organization** (code: DEFAULT_ORG)

### System Roles
1. SUPER_ADMIN - Full system access
2. SUPERVISOR - Manage organization and tasks
3. USER - Standard task management
4. GUEST - Read-only access

### Users
1. **Super Admin**
   - Email: admin@tasktracker.com
   - Role: SUPER_ADMIN
   - Status: APPROVED

2. **Regular User**
   - Email: user@tasktracker.com
   - Role: USER
   - Status: APPROVED

⚠️ **Note:** Password hashes are placeholders. Implement BCryptPasswordEncoder before production!

---

## 🚀 Available Endpoints

### Actuator (Public Access)
- `/api/actuator/health` - Application health status
- `/api/actuator/info` - Application information
- `/api/actuator/metrics` - Application metrics
- `/api/actuator/prometheus` - Prometheus metrics

---

## 🏗️ Project Structure

```
src/main/java/com/tasktracker/gamify/
├── TaskTrackerGamifyApplication.java
├── config/
│   ├── DataInitializerConfig.java    - Default data seeding
│   └── SecurityConfig.java            - Security configuration
├── entity/                            - JPA entities with Lombok
│   ├── Organization.java
│   ├── SystemRole.java
│   ├── UserInfo.java
│   └── UserRole.java
├── enums/
│   ├── SystemRoleCode.java
│   └── UserStatus.java
└── repository/                        - Spring Data JPA repositories
    ├── OrganizationRepository.java
    ├── SystemRoleRepository.java
    ├── UserInfoRepository.java
    └── UserRoleRepository.java

src/main/resources/
├── application.yml                    - Main configuration
└── db/changelog/
    ├── db.changelog-master.xml
    └── changes/
        ├── v1.0.0-baseline.xml
        └── v1.1.0-domain-model.xml    - Domain tables schema
```

---

## 🔧 Lombok Integration

All entity classes use Lombok annotations to reduce boilerplate:

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
```

**Benefits:**
- 400+ lines of boilerplate removed
- Automatic getter/setter generation
- Builder pattern support
- Clean, maintainable code

**Security Features:**
- `@ToString.Exclude` on `passwordHash` fields (never logged)
- `@ToString.Exclude` on lazy relationships (prevents LazyInitializationException)

---

## 🧪 Testing

### Health Check
```bash
curl http://localhost:8080/api/actuator/health
```

### Database Verification
```sql
-- Connect to PostgreSQL
psql -h localhost -p 5432 -U task_user -d taskdb

-- Check tables
SELECT * FROM organizations;
SELECT * FROM system_roles;
SELECT * FROM user_info;
SELECT * FROM user_roles;
```

### Test Scripts
- `./start-app.sh` - Clean startup with port cleanup
- `./test-endpoints.sh` - Test all actuator endpoints
- `./quick-test.sh` - Full integration test

---

## ⚠️ Important Notes

### Security
🔴 **CRITICAL:** Current password hashes are PLACEHOLDERS!

Before production:
1. Implement BCryptPasswordEncoder
2. Hash all passwords properly
3. Update DataInitializerConfig
4. Never store plain text passwords

Example:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### Mail Configuration
Mail health check is currently disabled. To enable:
1. Configure SMTP credentials in `application.yml`
2. Set `management.health.mail.enabled: true`

---

## 🎯 Architecture Highlights

### SOLID Principles Applied
- ✅ Single Responsibility - Each class has one purpose
- ✅ Open/Closed - Extensible without modification
- ✅ Liskov Substitution - All follow contracts
- ✅ Interface Segregation - Focused repositories
- ✅ Dependency Inversion - Depend on abstractions

### JPA Best Practices
- ✅ Lazy loading on all relationships
- ✅ Strategic indexing for performance
- ✅ Database constraints for data integrity
- ✅ Soft delete pattern for audit trail
- ✅ Auto-timestamps with @PrePersist/@PreUpdate
- ✅ Bean Validation annotations
- ✅ Composite unique constraints

---

## 📚 Additional Documentation

See `SETUP-GUIDE.md` for detailed:
- Configuration instructions
- Database setup
- Troubleshooting
- Production checklist

See `DEVELOPMENT-NOTES.md` for:
- Lombok usage examples
- Development tips
- Common issues and solutions

---

## 🎉 Project Status

✅ **Phase 1 Complete:** Base infrastructure and configuration
✅ **Phase 2 Complete:** Domain model (Users, Roles, Organizations)
✅ **Lombok Integration:** All entities refactored

### Next Development Steps
1. Implement BCryptPasswordEncoder
2. Create DTOs with MapStruct
3. Implement Service layer
4. Build REST API controllers
5. Add comprehensive tests
6. Implement authentication/authorization

---

## 📞 Support

For issues:
1. Check `SETUP-GUIDE.md` for configuration problems
2. Check `DEVELOPMENT-NOTES.md` for development issues
3. Review application logs
4. Verify PostgreSQL is running

---

**Version:** 0.0.1-SNAPSHOT  
**Last Updated:** January 19, 2026
