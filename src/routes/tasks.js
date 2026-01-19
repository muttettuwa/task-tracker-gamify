const express = require('express');
const router = express.Router();
const taskService = require('../services/taskService');

// Create a new task
router.post('/', (req, res) => {
  try {
    const { userId, title, description, priority, difficulty } = req.body;
    const task = taskService.createTask(userId, title, description, priority, difficulty);
    res.status(201).json(task);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Get all tasks (optionally filtered by userId)
router.get('/', (req, res) => {
  try {
    const { userId } = req.query;
    const tasks = taskService.getAllTasks(userId ? parseInt(userId) : null);
    res.json(tasks);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get a specific task
router.get('/:id', (req, res) => {
  try {
    const task = taskService.getTask(req.params.id);
    res.json(task);
  } catch (error) {
    res.status(404).json({ error: error.message });
  }
});

// Update a task
router.put('/:id', (req, res) => {
  try {
    const task = taskService.updateTask(req.params.id, req.body);
    res.json(task);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Delete a task
router.delete('/:id', (req, res) => {
  try {
    const task = taskService.deleteTask(req.params.id);
    res.json({ message: 'Task deleted successfully', task });
  } catch (error) {
    res.status(404).json({ error: error.message });
  }
});

// Get task statistics for a user
router.get('/stats/:userId', (req, res) => {
  try {
    const stats = taskService.getTaskStats(parseInt(req.params.userId));
    res.json(stats);
  } catch (error) {
    res.status(404).json({ error: error.message });
  }
});

module.exports = router;
