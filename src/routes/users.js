const express = require('express');
const router = express.Router();
const userService = require('../services/userService');

// Create a new user
router.post('/', (req, res) => {
  try {
    const { username } = req.body;
    const user = userService.createUser(username);
    res.status(201).json(user);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Get all users
router.get('/', (req, res) => {
  try {
    const users = userService.getAllUsers();
    res.json(users);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get a specific user
router.get('/:id', (req, res) => {
  try {
    const user = userService.getUser(req.params.id);
    res.json(user);
  } catch (error) {
    res.status(404).json({ error: error.message });
  }
});

// Get user's achievements
router.get('/:id/achievements', (req, res) => {
  try {
    const user = userService.getUser(req.params.id);
    res.json({
      userId: user.id,
      username: user.username,
      achievements: user.achievements
    });
  } catch (error) {
    res.status(404).json({ error: error.message });
  }
});

// Get leaderboard
router.get('/leaderboard/all', (req, res) => {
  try {
    const leaderboard = userService.getLeaderboard();
    res.json(leaderboard);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;
