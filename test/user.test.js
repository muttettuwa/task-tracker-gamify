const { test, describe } = require('node:test');
const assert = require('node:assert');
const User = require('../src/models/User');

describe('User Model', () => {
  test('should create a user with correct initial values', () => {
    const user = new User(1, 'testuser');
    
    assert.strictEqual(user.id, 1);
    assert.strictEqual(user.username, 'testuser');
    assert.strictEqual(user.points, 0);
    assert.strictEqual(user.level, 1);
    assert.deepStrictEqual(user.achievements, []);
  });

  test('should add points correctly', () => {
    const user = new User(1, 'testuser');
    user.addPoints(50);
    
    assert.strictEqual(user.points, 50);
  });

  test('should update level when points reach threshold', () => {
    const user = new User(1, 'testuser');
    user.addPoints(100);
    
    assert.strictEqual(user.level, 2);
    assert.strictEqual(user.points, 100);
  });

  test('should add achievement', () => {
    const user = new User(1, 'testuser');
    user.addAchievement('First Steps');
    
    assert.strictEqual(user.achievements.length, 1);
    assert.strictEqual(user.achievements[0], 'First Steps');
  });

  test('should not add duplicate achievements', () => {
    const user = new User(1, 'testuser');
    user.addAchievement('First Steps');
    user.addAchievement('First Steps');
    
    assert.strictEqual(user.achievements.length, 1);
  });
});
