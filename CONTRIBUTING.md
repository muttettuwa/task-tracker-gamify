# Contributing to Task Tracker Gamify

Thank you for your interest in contributing to Task Tracker Gamify! 

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 15+
- Git

### Setup Development Environment

1. **Fork the repository**
   ```bash
   # Click "Fork" on GitHub
   ```

2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR_USERNAME/task-tracker-gamify.git
   cd task-tracker-gamify
   ```

3. **Add upstream remote**
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/task-tracker-gamify.git
   ```

4. **Create a branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

---

## 📝 Development Guidelines

### Code Style
- Follow Spring Boot best practices
- Use Lombok for boilerplate reduction
- Follow SOLID principles
- Write clean, readable code
- Add JavaDoc comments for public methods

### Naming Conventions
- **Classes:** PascalCase (e.g., `UserRegistrationService`)
- **Methods:** camelCase (e.g., `registerNewUser()`)
- **Constants:** UPPER_SNAKE_CASE (e.g., `DEFAULT_ORGANIZATION_CODE`)
- **Variables:** camelCase (e.g., `userInfo`)

### Code Quality
- **No code smells** - SonarQube Grade A
- **No deprecation warnings**
- **Comprehensive exception handling**
- **Proper logging** (SLF4J)
- **Input validation** (Bean Validation)

---

## 🧪 Testing

### Requirements
- All new features must include tests
- Maintain 100% test coverage
- Add tests to Postman collection

### Running Tests
```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run Postman tests
# Import collection and run
```

---

## 🔒 Security

### Security Requirements
- **Never commit secrets** - Use environment variables
- **Validate all inputs** - Bean Validation
- **Hash passwords** - BCrypt only
- **No PII in logs** - Use email masking
- **Follow OWASP Top 10**

### Reporting Security Issues
Please report security vulnerabilities to [security@example.com] instead of creating public issues.

---

## 📦 Commit Guidelines

### Commit Message Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding/updating tests
- `chore`: Maintenance tasks

### Examples
```bash
feat(auth): add user registration endpoint

- Add RegisterUserRequest DTO with validation
- Implement UserRegistrationService
- Create user_approval table
- Add 25 comprehensive tests

Closes #123
```

```bash
fix(security): prevent email enumeration in login

- Return same error for invalid email and password
- Add email masking in logs
- Update security tests

Fixes #456
```

---

## 🔄 Pull Request Process

### Before Submitting
1. **Update your fork**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run tests**
   ```bash
   ./mvnw clean test
   ```

3. **Check code quality**
   ```bash
   ./mvnw clean compile
   # Ensure no deprecation warnings
   ```

4. **Update documentation**
   - Update README if needed
   - Add/update JavaDoc
   - Update CHANGELOG.md

### Submitting Pull Request
1. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create Pull Request**
   - Go to GitHub and click "New Pull Request"
   - Fill in the template
   - Link related issues

3. **PR Title Format**
   ```
   feat(scope): Brief description
   ```

### PR Checklist
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] No deprecation warnings
- [ ] Follows code style guidelines
- [ ] Commit messages follow convention

---

## 📚 Documentation

### Required Documentation
- **JavaDoc** - For all public methods
- **README** - Update if adding features
- **API Docs** - Update endpoint documentation
- **Tests** - Add test scenarios to Postman

### Documentation Style
```java
/**
 * Register a new user with approval workflow
 * 
 * @param request Registration request with user details
 * @return Registration response with user ID and status
 * @throws RegistrationException if registration fails
 */
public RegisterUserResponse registerNewUser(RegisterUserRequest request) {
    // Implementation
}
```

---

## 🐛 Bug Reports

### Creating Bug Reports
Include:
- **Description** - Clear description of the bug
- **Steps to reproduce** - Detailed steps
- **Expected behavior** - What should happen
- **Actual behavior** - What actually happens
- **Environment** - OS, Java version, etc.
- **Logs** - Relevant error logs

### Bug Report Template
```markdown
## Bug Description
Clear description of the bug

## Steps to Reproduce
1. Step one
2. Step two
3. See error

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: macOS 14.0
- Java: 17
- Spring Boot: 3.5.9
- Database: PostgreSQL 15

## Logs
```
Error log here
```
```

---

## 💡 Feature Requests

### Suggesting Features
Include:
- **Use case** - Why is this needed?
- **Proposed solution** - How should it work?
- **Alternatives** - Other possible solutions
- **Additional context** - Any other details

---

## 🎯 Areas for Contribution

### High Priority
- [ ] Email notification system
- [ ] Admin approval endpoints
- [ ] Email verification
- [ ] Password reset functionality

### Medium Priority
- [ ] Refresh tokens
- [ ] 2FA/MFA
- [ ] OAuth2 integration
- [ ] Swagger/OpenAPI documentation

### Nice to Have
- [ ] User profile management
- [ ] Organization management UI
- [ ] Activity logging
- [ ] Role hierarchy

---

## 🏗️ Architecture Guidelines

### Layered Architecture
```
Controller → Service → Repository → Database
     ↓          ↓
    DTO      Entity
```

### Design Principles
- **Interface-based** - Define service interfaces
- **Single Responsibility** - One class, one purpose
- **DRY** - Don't Repeat Yourself
- **KISS** - Keep It Simple, Stupid
- **SOLID** - Follow all SOLID principles

---

## 📞 Communication

### Getting Help
- **Documentation** - Check [DOCUMENTATION-INDEX.md](DOCUMENTATION-INDEX.md)
- **Issues** - Search existing issues first
- **Discussions** - Use GitHub Discussions for questions

### Code of Conduct
- Be respectful and professional
- Welcome newcomers
- Focus on constructive feedback
- No harassment or discrimination

---

## ✅ Review Process

### What We Look For
- **Code quality** - Clean, readable code
- **Tests** - Comprehensive test coverage
- **Documentation** - Clear documentation
- **Security** - No security issues
- **Performance** - No performance regressions

### Review Timeline
- Initial review: Within 3-5 days
- Follow-up: Within 2-3 days
- Merge: After all checks pass

---

## 🎉 Recognition

Contributors will be:
- Added to CONTRIBUTORS.md
- Mentioned in release notes
- Recognized in project documentation

---

## 📖 Resources

### Documentation
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JWT Best Practices](https://datatracker.ietf.org/doc/html/rfc8725)

### Tools
- [SonarQube](https://www.sonarqube.org/)
- [Postman](https://www.postman.com/)
- [Liquibase](https://www.liquibase.org/)

---

Thank you for contributing! 🚀
