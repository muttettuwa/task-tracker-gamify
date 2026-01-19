class Task {
  constructor(id, userId, title, description, priority = 'medium', difficulty = 'medium') {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.description = description;
    this.priority = priority; // low, medium, high
    this.difficulty = difficulty; // easy, medium, hard
    this.status = 'pending'; // pending, in-progress, completed
    this.points = this.calculatePoints();
    this.createdAt = new Date();
    this.completedAt = null;
  }

  calculatePoints() {
    const difficultyPoints = {
      easy: 10,
      medium: 20,
      hard: 30
    };
    const priorityMultiplier = {
      low: 1.0,
      medium: 1.5,
      high: 2.0
    };
    const basePoints = difficultyPoints[this.difficulty] || 20;
    const multiplier = priorityMultiplier[this.priority] || 1.5;
    return Math.floor(basePoints * multiplier);
  }

  updateStatus(newStatus) {
    this.status = newStatus;
    if (newStatus === 'completed') {
      this.completedAt = new Date();
    } else {
      this.completedAt = null;
    }
  }
}

module.exports = Task;
