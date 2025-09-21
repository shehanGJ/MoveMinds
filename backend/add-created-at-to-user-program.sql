-- Add created_at field to user_program table for proper revenue tracking
-- This migration adds a created_at timestamp to track when enrollments were made

USE move_minds;

-- Add created_at column to user_program table
ALTER TABLE user_program 
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Update existing records to have a created_at timestamp
-- Set to start_date for existing records
UPDATE user_program 
SET created_at = start_date 
WHERE created_at IS NULL;

-- Make the column NOT NULL after updating existing records
ALTER TABLE user_program 
MODIFY COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Add index for better query performance
CREATE INDEX idx_user_program_created_at ON user_program(created_at);

-- Verify the changes
DESCRIBE user_program;
