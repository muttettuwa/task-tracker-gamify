const Task = require('../models/Task');
const userService = require('./userService');

class TaskService {
  constructor() {
    this.tasks = new Map();
    this.currentId = 1;
  }

  createTask(userId, title, description, priority = 'medium', difficulty = 'medium') {
    if (!title || title.trim() === '') {
      throw new Error('Title is required');
    }

    // Verify user exists
    userService.getUser(userId);

    const task = new Task(this.currentId++, userId, title, description, priority, difficulty);
    this.tasks.set(task.id, task);
    return task;
  }

  getTask(id) {
    const task = this.tasks.get(parseInt(id));
    if (!task) {
      throw new Error('Task not found');
    }
    return task;
  }

  getAllTasks(userId = null) {
    const allTasks = Array.from(this.tasks.values());
    if (userId) {
      return allTasks.filter(task => task.userId === parseInt(userId));
    }
    return allTasks;
  }

  updateTask(id, updates) {
    const task = this.getTask(id);
    
    if (updates.title !== undefined) {
      if (!updates.title || updates.title.trim() === '') {
        throw new Error('Title cannot be empty');
      }
      task.title = updates.title;
    }
    
    if (updates.description !== undefined) {
      task.description = updates.description;
    }
    
    if (updates.priority !== undefined) {
      if (!['low', 'medium', 'high'].includes(updates.priority)) {
        throw new Error('Invalid priority');
      }
      task.priority = updates.priority;
      task.points = task.calculatePoints();
    }
    
    if (updates.difficulty !== undefined) {
      if (!['easy', 'medium', 'hard'].includes(updates.difficulty)) {
        throw new Error('Invalid difficulty');
      }
      task.difficulty = updates.difficulty;
      task.points = task.calculatePoints();
    }
    
    if (updates.status !== undefined) {
      if (!['pending', 'in-progress', 'completed'].includes(updates.status)) {
        throw new Error('Invalid status');
      }
      
      const wasCompleted = task.status === 'completed';
      const isNowCompleted = updates.status === 'completed';
      
      task.updateStatus(updates.status);
      
      // Award points when task is completed (but not if it was already completed)
      if (isNowCompleted && !wasCompleted) {
        userService.updateUserPoints(task.userId, task.points);
      }
    }
    
    return task;
  }

  deleteTask(id) {
    const task = this.getTask(id);
    this.tasks.delete(parseInt(id));
    return task;
  }

  getTaskStats(userId) {
    const userTasks = this.getAllTasks(userId);
    
    return {
      total: userTasks.length,
      pending: userTasks.filter(t => t.status === 'pending').length,
      inProgress: userTasks.filter(t => t.status === 'in-progress').length,
      completed: userTasks.filter(t => t.status === 'completed').length,
      totalPoints: userTasks
        .filter(t => t.status === 'completed')
        .reduce((sum, t) => sum + t.points, 0)
    };
  }
}

module.exports = new TaskService();
