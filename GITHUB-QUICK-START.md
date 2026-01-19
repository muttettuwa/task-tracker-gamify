# 🚀 GitHub Upload - Quick Reference

## Fastest Way to Upload

### Option 1: Automated Script (Recommended)
```bash
./upload-to-github.sh
```

Follow the prompts:
1. Create repository on GitHub first
2. Enter your GitHub username
3. Choose HTTPS or SSH
4. Script handles everything else!

---

### Option 2: Manual Commands

**1. Create repository on GitHub**
- Go to https://github.com/new
- Name: `task-tracker-gamify`
- **Don't** initialize with README
- Click "Create repository"

**2. Run these commands:**
```bash
cd /Users/madushankasrimalmuttettuwa/Partition_02/tasktracker.gamify/task-tracker-gamify

# Initialize git (if needed)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: JWT authentication system"

# Rename branch
git branch -M main

# Add remote (HTTPS)
git remote add origin https://github.com/YOUR_USERNAME/task-tracker-gamify.git

# Or for SSH
git remote add origin git@github.com:YOUR_USERNAME/task-tracker-gamify.git

# Push
git push -u origin main
```

**3. Enter credentials when prompted**
- Username: Your GitHub username
- Password: Personal Access Token (get from GitHub Settings)

**4. Done!** 🎉

---

## Personal Access Token

**How to create:**
1. GitHub → Settings → Developer settings
2. Personal access tokens → Tokens (classic)
3. Generate new token
4. Name: `task-tracker-upload`
5. Select scopes: `repo` (all)
6. Generate token
7. **Copy token** (you won't see it again!)

---

## After Upload

### Add Description
1. Go to your repository
2. Click gear icon (⚙️) next to "About"
3. Description: `Enterprise-grade JWT authentication backend with Spring Boot 3.5.9`
4. Add topics: `spring-boot`, `jwt`, `authentication`, `postgresql`, `rest-api`, `java`

### Create First Release
```bash
git tag -a v1.2.0 -m "Release v1.2.0: User Registration System"
git push origin v1.2.0
```

Then create release on GitHub.

---

## Troubleshooting

**"Permission denied"**
→ Use Personal Access Token instead of password

**"Repository not found"**
→ Check repository name and your username

**"failed to push"**
→ Run: `git pull origin main --rebase` then push again

---

## Your Repository URL
```
https://github.com/YOUR_USERNAME/task-tracker-gamify
```

Replace `YOUR_USERNAME` with your actual GitHub username.

---

**Need detailed guide?** See [GITHUB-UPLOAD-GUIDE.md](GITHUB-UPLOAD-GUIDE.md)

**Last Updated:** January 19, 2026
