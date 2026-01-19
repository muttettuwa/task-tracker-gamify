class User {
  constructor(id, username) {
    this.id = id;
    this.username = username;
    this.points = 0;
    this.level = 1;
    this.achievements = [];
    this.createdAt = new Date();
  }

  addPoints(points) {
    this.points += points;
    this.updateLevel();
  }

  updateLevel() {
    const newLevel = Math.floor(this.points / 100) + 1;
    if (newLevel > this.level) {
      this.level = newLevel;
      return true;
    }
    return false;
  }

  addAchievement(achievement) {
    if (!this.achievements.includes(achievement)) {
      this.achievements.push(achievement);
    }
  }
}

module.exports = User;
