# Task Tracker & Gamification Backend Service

A RESTful backend service that combines task management with gamification features to make productivity more engaging and rewarding.

## Features

- **Task Management**: Create, read, update, and delete tasks with priorities and difficulty levels
- **Gamification System**: Earn points and level up as you complete tasks
- **Achievement System**: Unlock achievements based on progress and milestones
- **Leaderboard**: Compete with other users based on points and levels
- **Task Statistics**: Track your productivity with detailed stats

## Getting Started

### Prerequisites

- Node.js 18.0.0 or higher

### Installation

1. Clone the repository:
```bash
git clone https://github.com/muttettuwa/task-tracker-gamify.git
cd task-tracker-gamify
```

2. Install dependencies:
```bash
npm install
```

3. Start the server:
```bash
npm start
```

The server will start on `http://localhost:3000` by default.

### Development Mode

Run the server in development mode with auto-reload:
```bash
npm run dev
```

## API Documentation

### Health Check

**GET** `/health`

Check if the service is running.

**Response:**
```json
{
  "status": "healthy",
  "timestamp": "2026-01-19T22:00:00.000Z",
  "service": "Task Tracker & Gamification Backend"
}
```

### Users

#### Create User

**POST** `/api/users`

Create a new user account.

**Request Body:**
```json
{
  "username": "johndoe"
}
```

**Response:** (201 Created)
```json
{
  "id": 1,
  "username": "johndoe",
  "points": 0,
  "level": 1,
  "achievements": [],
  "createdAt": "2026-01-19T22:00:00.000Z"
}
```

#### Get All Users

**GET** `/api/users`

Retrieve all users.

**Response:**
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "points": 150,
    "level": 2,
    "achievements": ["First Steps"],
    "createdAt": "2026-01-19T22:00:00.000Z"
  }
]
```

#### Get User

**GET** `/api/users/:id`

Get a specific user by ID.

**Response:**
```json
{
  "id": 1,
  "username": "johndoe",
  "points": 150,
  "level": 2,
  "achievements": ["First Steps"],
  "createdAt": "2026-01-19T22:00:00.000Z"
}
```

#### Get User Achievements

**GET** `/api/users/:id/achievements`

Get all achievements for a specific user.

**Response:**
```json
{
  "userId": 1,
  "username": "johndoe",
  "achievements": ["First Steps", "Rising Star"]
}
```

#### Get Leaderboard

**GET** `/api/users/leaderboard/all`

Get the leaderboard sorted by points.

**Response:**
```json
[
  {
    "rank": 1,
    "username": "johndoe",
    "points": 500,
    "level": 5
  },
  {
    "rank": 2,
    "username": "janedoe",
    "points": 300,
    "level": 3
  }
]
```

### Tasks

#### Create Task

**POST** `/api/tasks`

Create a new task.

**Request Body:**
```json
{
  "userId": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive API docs",
  "priority": "high",
  "difficulty": "medium"
}
```

**Parameters:**
- `userId` (required): User ID
- `title` (required): Task title
- `description` (optional): Task description
- `priority` (optional): `low`, `medium`, or `high` (default: `medium`)
- `difficulty` (optional): `easy`, `medium`, or `hard` (default: `medium`)

**Response:** (201 Created)
```json
{
  "id": 1,
  "userId": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive API docs",
  "priority": "high",
  "difficulty": "medium",
  "status": "pending",
  "points": 30,
  "createdAt": "2026-01-19T22:00:00.000Z",
  "completedAt": null
}
```

#### Get All Tasks

**GET** `/api/tasks`

Get all tasks, optionally filtered by user.

**Query Parameters:**
- `userId` (optional): Filter tasks by user ID

**Response:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "title": "Complete project documentation",
    "description": "Write comprehensive API docs",
    "priority": "high",
    "difficulty": "medium",
    "status": "completed",
    "points": 30,
    "createdAt": "2026-01-19T22:00:00.000Z",
    "completedAt": "2026-01-19T23:00:00.000Z"
  }
]
```

#### Get Task

**GET** `/api/tasks/:id`

Get a specific task by ID.

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive API docs",
  "priority": "high",
  "difficulty": "medium",
  "status": "completed",
  "points": 30,
  "createdAt": "2026-01-19T22:00:00.000Z",
  "completedAt": "2026-01-19T23:00:00.000Z"
}
```

#### Update Task

**PUT** `/api/tasks/:id`

Update an existing task. When a task is marked as completed, the user automatically earns points.

**Request Body:**
```json
{
  "status": "completed"
}
```

**Updatable Fields:**
- `title`: Task title
- `description`: Task description
- `priority`: Task priority (`low`, `medium`, `high`)
- `difficulty`: Task difficulty (`easy`, `medium`, `hard`)
- `status`: Task status (`pending`, `in-progress`, `completed`)

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "title": "Complete project documentation",
  "description": "Write comprehensive API docs",
  "priority": "high",
  "difficulty": "medium",
  "status": "completed",
  "points": 30,
  "createdAt": "2026-01-19T22:00:00.000Z",
  "completedAt": "2026-01-19T23:00:00.000Z"
}
```

#### Delete Task

**DELETE** `/api/tasks/:id`

Delete a task.

**Response:**
```json
{
  "message": "Task deleted successfully",
  "task": {
    "id": 1,
    "userId": 1,
    "title": "Complete project documentation",
    ...
  }
}
```

#### Get Task Statistics

**GET** `/api/tasks/stats/:userId`

Get task statistics for a specific user.

**Response:**
```json
{
  "total": 10,
  "pending": 2,
  "inProgress": 3,
  "completed": 5,
  "totalPoints": 150
}
```

## Gamification System

### Points System

Tasks award points based on difficulty and priority:

**Difficulty Points:**
- Easy: 10 points
- Medium: 20 points
- Hard: 30 points

**Priority Multiplier:**
- Low: 1.0x
- Medium: 1.5x
- High: 2.0x

**Example:** A hard task with high priority = 30 × 2.0 = 60 points

### Levels

Users level up every 100 points:
- Level 1: 0-99 points
- Level 2: 100-199 points
- Level 3: 200-299 points
- And so on...

### Achievements

Users can unlock various achievements:
- **First Steps**: Earn your first 10 points
- **Rising Star**: Reach level 5
- **Master Tasker**: Reach level 10
- **Point Collector**: Accumulate 500 points

## Testing

Run the test suite:
```bash
npm test
```

## Project Structure

```
task-tracker-gamify/
├── src/
│   ├── models/
│   │   ├── User.js          # User model
│   │   └── Task.js          # Task model
│   ├── services/
│   │   ├── userService.js   # User business logic
│   │   └── taskService.js   # Task business logic
│   ├── routes/
│   │   ├── users.js         # User API routes
│   │   └── tasks.js         # Task API routes
│   └── server.js            # Express server setup
├── test/
│   ├── user.test.js         # User model tests
│   └── task.test.js         # Task model tests
├── package.json
└── README.md
```

## Technologies Used

- **Node.js**: JavaScript runtime
- **Express.js**: Web application framework
- **Node.js Test Runner**: Built-in testing framework

## Data Storage

Currently uses in-memory storage. All data is lost when the server restarts. For production use, integrate a database like PostgreSQL, MongoDB, or MySQL.

## Future Enhancements

- Database integration for persistent storage
- User authentication and authorization
- Team/group functionality
- Task categories and tags
- Due dates and reminders
- More achievements and badges
- Activity feed and notifications
- Task templates
- API rate limiting
- WebSocket support for real-time updates

## License

MIT