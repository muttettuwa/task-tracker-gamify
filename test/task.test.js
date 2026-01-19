const { test, describe } = require('node:test');
const assert = require('node:assert');
const Task = require('../src/models/Task');

describe('Task Model', () => {
  test('should create a task with correct initial values', () => {
    const task = new Task(1, 1, 'Test Task', 'Test description');
    
    assert.strictEqual(task.id, 1);
    assert.strictEqual(task.userId, 1);
    assert.strictEqual(task.title, 'Test Task');
    assert.strictEqual(task.description, 'Test description');
    assert.strictEqual(task.status, 'pending');
    assert.strictEqual(task.priority, 'medium');
    assert.strictEqual(task.difficulty, 'medium');
  });

  test('should calculate points correctly for easy task', () => {
    const task = new Task(1, 1, 'Test', 'Desc', 'medium', 'easy');
    assert.strictEqual(task.points, 15); // 10 * 1.5
  });

  test('should calculate points correctly for hard task with high priority', () => {
    const task = new Task(1, 1, 'Test', 'Desc', 'high', 'hard');
    assert.strictEqual(task.points, 60); // 30 * 2.0
  });

  test('should update status correctly', () => {
    const task = new Task(1, 1, 'Test', 'Desc');
    task.updateStatus('in-progress');
    
    assert.strictEqual(task.status, 'in-progress');
    assert.strictEqual(task.completedAt, null);
  });

  test('should set completedAt when status is completed', () => {
    const task = new Task(1, 1, 'Test', 'Desc');
    task.updateStatus('completed');
    
    assert.strictEqual(task.status, 'completed');
    assert.ok(task.completedAt instanceof Date);
  });
});
