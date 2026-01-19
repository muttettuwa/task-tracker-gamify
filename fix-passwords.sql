-- Fix User Passwords with BCrypt Hashes
-- Run this script if you're getting authentication errors

-- Connect to database first:
-- psql -h localhost -p 5432 -U task_user -d taskdb

-- Check current password hashes
SELECT id, email, password_hash, status
FROM user_info
WHERE email IN ('admin@tasktracker.com', 'user@tasktracker.com');

-- Update admin password to BCrypt hash of "admin123"
UPDATE user_info
SET password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMy.JqPZPqVgFOV8cXF5CKpCCLqrC3mN8Mu'
WHERE email = 'admin@tasktracker.com';

-- Update user password to BCrypt hash of "user123"
UPDATE user_info
SET password_hash = '$2a$10$eImiTXuWVxfM37uY4JANjO0H9E7AfJUGqKQm9cOCLGJBfQjWg3Rp6'
WHERE email = 'user@tasktracker.com';

-- Verify the update
SELECT id, email, password_hash, status
FROM user_info
WHERE email IN ('admin@tasktracker.com', 'user@tasktracker.com');

-- Expected output:
-- password_hash should start with $2a$10$ (BCrypt format)
-- status should be 'APPROVED'

-- Notes:
-- admin123 BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMy.JqPZPqVgFOV8cXF5CKpCCLqrC3mN8Mu
-- user123 BCrypt hash:  $2a$10$eImiTXuWVxfM37uY4JANjO0H9E7AfJUGqKQm9cOCLGJBfQjWg3Rp6
