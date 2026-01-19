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
    return Math.floor(difficultyPoints[this.difficulty] * priorityMultiplier[this.priority]);
  }

  updateStatus(newStatus) {
    this.status = newStatus;
    if (newStatus === 'completed') {
      this.completedAt = new Date();
    }
  }
}

module.exports = Task;
