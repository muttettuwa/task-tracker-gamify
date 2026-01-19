# Development Notes & Reference

Technical reference for developers working on the Task Tracker Gamify project.

---

## 📋 Table of Contents
1. [Lombok Usage](#lombok-usage)
2. [Entity Patterns](#entity-patterns)
3. [Repository Patterns](#repository-patterns)
4. [Common Issues & Solutions](#common-issues--solutions)
5. [Code Examples](#code-examples)
6. [Development Tips](#development-tips)

---

## Lombok Usage

### Current Lombok Configuration

**Version:** 1.18.34

All entities use Lombok to reduce boilerplate code.

### Standard Entity Annotations

```java
@Entity
@Table(name = "table_name")
@Getter                          // Generates all getters
@Setter                          // Generates all setters
@NoArgsConstructor              // Required by JPA
@AllArgsConstructor             // Constructor with all fields
@Builder                        // Builder pattern support
@ToString                       // toString() method
@EqualsAndHashCode(of = "id")   // equals/hashCode based on ID
public class MyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Other fields...
}
```

### Important Lombok Patterns

#### 1. Exclude Sensitive Data from ToString

```java
@ToString  // No parameters at class level
public class UserInfo {
    @ToString.Exclude  // ✅ NEW STYLE - Use this
    private String passwordHash;
}
```

**❌ DON'T MIX STYLES:**
```java
@ToString(exclude = "passwordHash")  // ❌ Old style
public class UserInfo {
    @ToString.Exclude  // ❌ Mixing = ERROR
    private String other;
}
```

#### 2. Exclude Lazy Relationships

```java
public class UserInfo {
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude  // Prevents LazyInitializationException
    private Organization organization;
}
```

#### 3. Builder with Defaults

```java
public class Organization {
    @Builder.Default
    private Boolean isActive = true;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}

// Usage:
Organization org = Organization.builder()
    .name("Test Org")
    .code("TEST")
    // isActive defaults to true
    .build();
```

#### 4. Business Key Equality

```java
// Use business keys for equals/hashCode
@EqualsAndHashCode(of = {"id", "code"})  // ✅ Multiple fields
public class Organization { }

@EqualsAndHashCode(of = {"id", "email"})  // ✅ Natural key
public class UserInfo { }

@EqualsAndHashCode(of = "id")  // ✅ Simple ID-based
public class UserRole { }
```

### Lombok + MapStruct Integration

Already configured in `pom.xml`:

```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
    </path>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
    </path>
</annotationProcessorPaths>
```

This ensures Lombok runs before MapStruct, allowing MapStruct to use generated getters/setters.

---

## Entity Patterns

### Soft Delete Pattern

```java
@Entity
public class UserInfo {
    @Builder.Default
    @Column(name = "logically_deleted", nullable = false)
    private Boolean logicallyDeleted = false;
    
    // Repository methods filter by logicallyDeleted
}
```

**Usage:**
```java
// Don't actually delete, just mark as deleted
user.setLogicallyDeleted(true);
userRepository.save(user);

// Repository automatically filters
List<UserInfo> activeUsers = userRepository.findByLogicallyDeletedFalse();
```

### Auto-Timestamps Pattern

```java
@Entity
public class Organization {
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

### Enum Mapping Pattern

```java
@Entity
public class UserInfo {
    @Enumerated(EnumType.STRING)  // Store as string, not int
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;
}

// Enum definition
public enum UserStatus {
    PENDING("Pending approval"),
    APPROVED("Approved and active"),
    REJECTED("Rejected"),
    LOCKED("Account locked");
    
    private final String description;
    
    UserStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
```

---

## Repository Patterns

### Basic Query Methods

```java
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    // Derived query method - Spring Data JPA generates SQL
    Optional<UserInfo> findByEmail(String email);
    
    // Case-insensitive search
    Optional<UserInfo> findByEmailIgnoreCase(String email);
    
    // Existence check
    boolean existsByEmail(String email);
    
    // Multiple conditions
    List<UserInfo> findByOrganizationAndStatusAndLogicallyDeletedFalse(
        Organization organization, UserStatus status);
}
```

### Custom JPQL Queries

```java
@Query("SELECT u FROM UserInfo u WHERE u.logicallyDeleted = false AND " +
       "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
       "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
List<UserInfo> searchUsers(@Param("searchTerm") String searchTerm);
```

### Count Queries

```java
@Query("SELECT COUNT(u) FROM UserInfo u WHERE u.organization = :organization " +
       "AND u.status = 'APPROVED' AND u.logicallyDeleted = false")
long countActiveUsersByOrganization(@Param("organization") Organization organization);
```

### Complex Join Queries

```java
@Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur " +
       "WHERE ur.userInfo = :userInfo AND ur.organization = :organization " +
       "AND ur.systemRole.code = :roleCode AND ur.active = true")
boolean hasRole(@Param("userInfo") UserInfo userInfo, 
                @Param("organization") Organization organization,
                @Param("roleCode") SystemRoleCode roleCode);
```

---

## Common Issues & Solutions

### Issue: Lombok Annotations Not Recognized

**Symptom:** IDE shows errors like "Cannot resolve symbol 'Getter'"

**Solution:**
1. Install Lombok plugin in IDE
2. Enable annotation processing
3. Restart IDE
4. Note: Code still compiles even without plugin!

### Issue: LazyInitializationException

**Symptom:**
```
org.hibernate.LazyInitializationException: could not initialize proxy
```

**Solutions:**

1. **Exclude from toString:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@ToString.Exclude  // ✅ Prevents exception in toString
private Organization organization;
```

2. **Use @Transactional:**
```java
@Service
@Transactional(readOnly = true)
public class UserService {
    public UserInfo getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
```

3. **Fetch eagerly when needed:**
```java
@Query("SELECT u FROM UserInfo u LEFT JOIN FETCH u.organization WHERE u.id = :id")
Optional<UserInfo> findByIdWithOrganization(@Param("id") Long id);
```

### Issue: Hibernate N+1 Query Problem

**Symptom:** Too many database queries

**Solutions:**

1. **Use JOIN FETCH:**
```java
@Query("SELECT ur FROM UserRole ur " +
       "LEFT JOIN FETCH ur.userInfo " +
       "LEFT JOIN FETCH ur.organization " +
       "LEFT JOIN FETCH ur.systemRole " +
       "WHERE ur.active = true")
List<UserRole> findAllActiveWithRelations();
```

2. **Use @EntityGraph:**
```java
@EntityGraph(attributePaths = {"organization", "systemRole"})
List<UserRole> findByUserInfo(UserInfo userInfo);
```

### Issue: Unique Constraint Violation

**Symptom:**
```
ERROR: duplicate key value violates unique constraint "uk_email"
```

**Solution:**
Always check existence before saving:

```java
if (userRepository.existsByEmail(email)) {
    throw new IllegalArgumentException("Email already exists");
}
UserInfo user = new UserInfo(...);
userRepository.save(user);
```

---

## Code Examples

### Creating Entities with Builder

```java
// Using builder pattern (thanks to Lombok @Builder)
Organization org = Organization.builder()
    .name("Acme Corp")
    .code("ACME")
    .isActive(true)
    .build();
organizationRepository.save(org);

UserInfo user = UserInfo.builder()
    .organization(org)
    .firstName("John")
    .lastName("Doe")
    .email("john.doe@example.com")
    .passwordHash(passwordEncoder.encode("password"))
    .status(UserStatus.APPROVED)
    .telephone("+1234567890")
    .build();
userRepository.save(user);

SystemRole role = systemRoleRepository.findByCode(SystemRoleCode.USER)
    .orElseThrow();

UserRole userRole = UserRole.builder()
    .userInfo(user)
    .organization(org)
    .systemRole(role)
    .active(true)
    .build();
userRoleRepository.save(userRole);
```

### Querying with Specifications

For complex queries, consider using Specifications:

```java
public interface UserInfoRepository extends JpaRepository<UserInfo, Long>, 
                                           JpaSpecificationExecutor<UserInfo> {
}

// Usage
Specification<UserInfo> spec = (root, query, cb) -> {
    List<Predicate> predicates = new ArrayList<>();
    
    predicates.add(cb.equal(root.get("logicallyDeleted"), false));
    
    if (organizationId != null) {
        predicates.add(cb.equal(root.get("organization").get("id"), organizationId));
    }
    
    if (status != null) {
        predicates.add(cb.equal(root.get("status"), status));
    }
    
    return cb.and(predicates.toArray(new Predicate[0]));
};

List<UserInfo> users = userRepository.findAll(spec);
```

### Transaction Management

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserInfoRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    
    @Transactional
    public UserInfo createUserWithRole(CreateUserRequest request) {
        // All operations in single transaction
        UserInfo user = UserInfo.builder()
            .email(request.getEmail())
            // ... other fields
            .build();
        user = userRepository.save(user);
        
        UserRole userRole = UserRole.builder()
            .userInfo(user)
            // ... other fields
            .build();
        userRoleRepository.save(userRole);
        
        return user;
        // Transaction commits here, or rolls back on exception
    }
}
```

---

## Development Tips

### 1. Use Profiles for Different Environments

```yaml
# application-dev.yml
spring:
  jpa:
    show-sql: true
logging:
  level:
    com.tasktracker.gamify: DEBUG

# application-prod.yml
spring:
  jpa:
    show-sql: false
logging:
  level:
    root: WARN
```

Run with: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

### 2. Enable SQL Logging for Debugging

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### 3. Use CommandLineRunner for Testing

```java
@Component
public class DataTester implements CommandLineRunner {
    @Autowired
    private UserInfoRepository userRepository;
    
    @Override
    public void run(String... args) {
        // Quick test code
        List<UserInfo> users = userRepository.findAll();
        System.out.println("Total users: " + users.size());
    }
}
```

### 4. Validate Entities Before Saving

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final Validator validator;
    private final UserInfoRepository repository;
    
    public UserInfo save(UserInfo user) {
        Set<ConstraintViolation<UserInfo>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return repository.save(user);
    }
}
```

### 5. Custom Repository Methods

```java
public interface CustomUserInfoRepository {
    List<UserInfo> findUsersWithCustomLogic(SearchCriteria criteria);
}

@Repository
public class CustomUserInfoRepositoryImpl implements CustomUserInfoRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<UserInfo> findUsersWithCustomLogic(SearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserInfo> query = cb.createQuery(UserInfo.class);
        // ... custom logic
        return entityManager.createQuery(query).getResultList();
    }
}

// Extend in main repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long>, 
                                           CustomUserInfoRepository {
}
```

---

## Testing Patterns

### Repository Tests

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserInfoRepositoryTest {
    @Autowired
    private UserInfoRepository repository;
    
    @Test
    void shouldFindUserByEmail() {
        // Arrange
        UserInfo user = UserInfo.builder()
            .email("test@example.com")
            .build();
        repository.save(user);
        
        // Act
        Optional<UserInfo> found = repository.findByEmail("test@example.com");
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }
}
```

### Service Tests

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserInfoRepository repository;
    
    @InjectMocks
    private UserService service;
    
    @Test
    void shouldCreateUser() {
        // Arrange
        UserInfo user = UserInfo.builder().build();
        when(repository.save(any())).thenReturn(user);
        
        // Act
        UserInfo result = service.createUser(user);
        
        // Assert
        assertNotNull(result);
        verify(repository).save(user);
    }
}
```

---

## Useful SQL Queries

### Check Data Initialization

```sql
-- Verify all default data loaded
SELECT 'organizations' as table_name, COUNT(*) as count FROM organizations
UNION ALL
SELECT 'system_roles', COUNT(*) FROM system_roles
UNION ALL
SELECT 'user_info', COUNT(*) FROM user_info
UNION ALL
SELECT 'user_roles', COUNT(*) FROM user_roles;
```

### Find Orphaned Records

```sql
-- User roles without valid user
SELECT ur.* FROM user_roles ur
LEFT JOIN user_info ui ON ur.user_info_id = ui.id
WHERE ui.id IS NULL;
```

### Performance Analysis

```sql
-- Check index usage
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;

-- Table sizes
SELECT tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

---

## Quick Reference

### Common Lombok Annotations
- `@Getter` / `@Setter` - Generate getters/setters
- `@NoArgsConstructor` - No-args constructor (JPA requirement)
- `@AllArgsConstructor` - Constructor with all fields
- `@Builder` - Builder pattern
- `@ToString` - toString() method
- `@EqualsAndHashCode` - equals()/hashCode()
- `@Data` - All of the above (use for DTOs)
- `@Value` - Immutable class
- `@Slf4j` - Logger instance

### JPA Annotations
- `@Entity` - JPA entity
- `@Table` - Table configuration
- `@Id` - Primary key
- `@GeneratedValue` - Auto-generated value
- `@Column` - Column configuration
- `@ManyToOne` / `@OneToMany` - Relationships
- `@Enumerated` - Enum mapping
- `@PrePersist` / `@PreUpdate` - Lifecycle callbacks

### Validation Annotations
- `@NotNull` - Field cannot be null
- `@NotBlank` - String cannot be blank
- `@Email` - Valid email format
- `@Size` - String length constraint
- `@Past` - Date in the past
- `@Pattern` - Regex validation

---

**For setup instructions, see SETUP-GUIDE.md**  
**For project overview, see README.md**
