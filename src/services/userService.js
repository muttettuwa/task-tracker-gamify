const User = require('../models/User');

class UserService {
  constructor() {
    this.users = new Map();
    this.currentId = 1;
  }

  createUser(username) {
    if (!username || username.trim() === '') {
      throw new Error('Username is required');
    }

    // Check if username already exists
    const existingUser = Array.from(this.users.values()).find(
      user => user.username === username
    );
    if (existingUser) {
      throw new Error('Username already exists');
    }

    const user = new User(this.currentId++, username);
    this.users.set(user.id, user);
    return user;
  }

  getUser(id) {
    const user = this.users.get(parseInt(id));
    if (!user) {
      throw new Error('User not found');
    }
    return user;
  }

  getAllUsers() {
    return Array.from(this.users.values());
  }

  updateUserPoints(userId, points) {
    const user = this.getUser(userId);
    const leveledUp = user.updateLevel();
    user.addPoints(points);
    
    // Check for achievements
    this.checkAchievements(user);
    
    return { user, leveledUp };
  }

  checkAchievements(user) {
    // First task achievement
    if (user.points >= 10 && !user.achievements.includes('First Steps')) {
      user.addAchievement('First Steps');
    }
    
    // Level achievements
    if (user.level >= 5 && !user.achievements.includes('Rising Star')) {
      user.addAchievement('Rising Star');
    }
    
    if (user.level >= 10 && !user.achievements.includes('Master Tasker')) {
      user.addAchievement('Master Tasker');
    }
    
    // Points milestones
    if (user.points >= 500 && !user.achievements.includes('Point Collector')) {
      user.addAchievement('Point Collector');
    }
  }

  getLeaderboard() {
    return Array.from(this.users.values())
      .sort((a, b) => b.points - a.points)
      .map((user, index) => ({
        rank: index + 1,
        username: user.username,
        points: user.points,
        level: user.level
      }));
  }
}

module.exports = new UserService();
