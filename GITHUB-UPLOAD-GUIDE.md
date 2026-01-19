# 🚀 GitHub Upload Guide - Task Tracker Gamify

Complete step-by-step guide to upload this project to GitHub.

---

## 📋 Prerequisites

Before uploading to GitHub, ensure you have:

- ✅ Git installed on your system
- ✅ GitHub account created
- ✅ SSH keys configured (recommended) OR HTTPS credentials

---

## 🎯 Step-by-Step Upload Instructions

### Step 1: Initialize Git Repository (if not already done)

```bash
cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify

# Check if git is already initialized
git status

# If not initialized, run:
git init
```

### Step 2: Configure Git (First Time Only)

```bash
# Set your name
git config --global user.name "Your Name"

# Set your email
git config --global user.email "your.email@example.com"
```

### Step 3: Create Repository on GitHub

1. Go to https://github.com
2. Click the **"+"** icon (top right) → **"New repository"**
3. Fill in repository details:
   - **Repository name:** `task-tracker-gamify` (or your preferred name)
   - **Description:** `Enterprise-grade JWT authentication backend with Spring Boot 3.5.9`
   - **Visibility:** Choose Public or Private
   - **DO NOT** initialize with README, .gitignore, or license (we already have these)
4. Click **"Create repository"**

### Step 4: Add All Files to Git

```bash
# Add all files (respects .gitignore)
git add .

# Check what will be committed
git status

# Commit with descriptive message
git commit -m "Initial commit: Complete JWT authentication system with user registration

- JWT token-based authentication
- User registration with approval workflow
- Role-based access control (SUPER_ADMIN, SUPERVISOR, USER)
- PostgreSQL database with Liquibase migrations
- 82 comprehensive tests (100% passing)
- SonarQube Grade A code quality
- Complete documentation"
```

### Step 5: Connect to GitHub Repository

**Option A: Using HTTPS (Easier)**
```bash
# Replace YOUR_USERNAME with your GitHub username
git remote add origin https://github.com/YOUR_USERNAME/task-tracker-gamify.git

# Verify remote
git remote -v
```

**Option B: Using SSH (Recommended for frequent use)**
```bash
# Replace YOUR_USERNAME with your GitHub username
git remote add origin git@github.com:YOUR_USERNAME/task-tracker-gamify.git

# Verify remote
git remote -v
```

### Step 6: Push to GitHub

```bash
# Rename branch to main (if currently master)
git branch -M main

# Push to GitHub
git push -u origin main
```

**If prompted for credentials (HTTPS):**
- Username: Your GitHub username
- Password: Your GitHub Personal Access Token (NOT your GitHub password)

**Creating Personal Access Token:**
1. GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token
3. Select scopes: `repo` (full control)
4. Copy token (you won't see it again!)

---

## 🔐 Recommended: Set Up SSH Keys (One-time Setup)

### Generate SSH Key
```bash
# Generate new SSH key
ssh-keygen -t ed25519 -C "your.email@example.com"

# Press Enter for default location
# Optionally set a passphrase

# Start SSH agent
eval "$(ssh-agent -s)"

# Add SSH key
ssh-add ~/.ssh/id_ed25519
```

### Add SSH Key to GitHub
```bash
# Copy public key to clipboard
cat ~/.ssh/id_ed25519.pub | pbcopy
# On macOS, or manually copy the output
```

1. Go to GitHub → Settings → SSH and GPG keys
2. Click "New SSH key"
3. Paste the key
4. Click "Add SSH key"

### Test Connection
```bash
ssh -T git@github.com
# Should see: "Hi username! You've successfully authenticated..."
```

---

## 📁 What Gets Uploaded

### ✅ Files Included (See .gitignore)
- Source code (`src/`)
- Configuration files (`application.yml`, `pom.xml`)
- Documentation (All .md files)
- Database migrations (`src/main/resources/db/`)
- Postman collection
- Shell scripts (`*.sh`)
- Maven wrapper (`mvnw`, `mvnw.cmd`)
- `.gitignore`, `LICENSE`, `CONTRIBUTING.md`

### ❌ Files Excluded (Per .gitignore)
- `target/` - Build output
- `.idea/`, `*.iml` - IDE files
- `.DS_Store` - OS files
- `*.log` - Log files
- `*.class`, `*.jar` - Compiled files
- `*.postman_test_run.json` - Test results
- Environment-specific configs

---

## 🎨 Customize Your Repository

### Add Repository Description
1. Go to your repository on GitHub
2. Click the gear icon (⚙️) next to "About"
3. Add description: `Enterprise-grade JWT authentication backend with Spring Boot 3.5.9`
4. Add website (if any)
5. Add topics: `spring-boot`, `jwt`, `authentication`, `postgresql`, `rest-api`, `java`, `security`

### Add README Badges (Optional)
The README-GITHUB.md already includes badges. You can customize them:
- Build status
- Code coverage
- License
- Latest release

### Enable GitHub Pages (Optional)
For documentation hosting:
1. Settings → Pages
2. Source: Deploy from branch
3. Branch: main, folder: /docs
4. Save

---

## 🔄 Future Updates

### After Making Changes

```bash
# Check status
git status

# Add changes
git add .

# Commit
git commit -m "feat(registration): add email verification

- Implement email verification service
- Add verification token generation
- Create email templates
- Add tests for verification flow"

# Push
git push origin main
```

### Create Feature Branches

```bash
# Create and switch to feature branch
git checkout -b feature/email-verification

# Make changes, commit

# Push feature branch
git push origin feature/email-verification

# Create Pull Request on GitHub
```

---

## 🏷️ Create Releases

### Tag a Version
```bash
# Create tag
git tag -a v1.2.0 -m "Release v1.2.0: User Registration System

New Features:
- User registration with validation
- Approval workflow
- 25 new test scenarios

Improvements:
- Enhanced security
- Better error handling
- Comprehensive documentation"

# Push tag
git push origin v1.2.0
```

### Create GitHub Release
1. Go to repository → Releases
2. Click "Draft a new release"
3. Choose tag: v1.2.0
4. Title: "v1.2.0 - User Registration System"
5. Description: Release notes
6. Attach files (if any)
7. Publish release

---

## 📊 Repository Settings Recommendations

### Branch Protection (Recommended for teams)
Settings → Branches → Add rule:
- Branch name pattern: `main`
- ✅ Require pull request reviews before merging
- ✅ Require status checks to pass before merging
- ✅ Require conversation resolution before merging

### GitHub Actions (Optional CI/CD)
Create `.github/workflows/maven.yml`:
```yaml
name: Java CI with Maven

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Build with Maven
      run: ./mvnw clean package
```

---

## 🚨 Important Security Notes

### ⚠️ Never Commit:
- Database passwords
- JWT secret keys
- API keys
- Personal Access Tokens
- Email credentials
- Any sensitive data

### Use Environment Variables:
```yaml
# application.yml - Use placeholders
jwt:
  secret: ${JWT_SECRET:default-secret-for-dev-only}
  expiration: ${JWT_EXPIRATION:86400000}

spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/taskdb}
    username: ${DB_USERNAME:task_user}
    password: ${DB_PASSWORD:task_secret}
```

### GitHub Secrets (For CI/CD):
Settings → Secrets and variables → Actions → New repository secret

---

## ✅ Verification Checklist

After upload, verify:

- [ ] Repository is created on GitHub
- [ ] All files are uploaded
- [ ] README displays correctly
- [ ] .gitignore is working (target/ not uploaded)
- [ ] License file is present
- [ ] Documentation is accessible
- [ ] Repository description is set
- [ ] Topics/tags are added
- [ ] CONTRIBUTING.md is visible

---

## 🎉 You're Done!

Your project is now on GitHub! 

### Share Your Repository
```
https://github.com/YOUR_USERNAME/task-tracker-gamify
```

### Next Steps:
1. ⭐ Star your own repository
2. 📝 Add more documentation
3. 🔄 Enable GitHub Actions for CI/CD
4. 🏷️ Create your first release (v1.2.0)
5. 📢 Share with the community

---

## 📞 Troubleshooting

### Common Issues

**"Permission denied (publickey)"**
- Solution: Set up SSH keys (see above)

**"Repository not found"**
- Solution: Check repository name and access permissions

**"failed to push some refs"**
- Solution: Pull latest changes first: `git pull origin main --rebase`

**Large files rejected**
- Solution: Add to .gitignore and remove from git:
  ```bash
  git rm --cached path/to/large/file
  git commit -m "Remove large file"
  ```

---

## 📚 Additional Resources

- [GitHub Docs](https://docs.github.com)
- [Git Documentation](https://git-scm.com/doc)
- [GitHub Desktop](https://desktop.github.com/) - GUI alternative
- [GitKraken](https://www.gitkraken.com/) - Another GUI option

---

**Your project is production-ready and ready to share with the world!** 🚀

Last Updated: January 19, 2026
