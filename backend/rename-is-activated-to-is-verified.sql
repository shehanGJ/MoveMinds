-- Rename is_activated column to is_verified and set all users to not verified
-- This migration renames the column and sets all users to false (not verified)

USE move_minds;

-- Rename the column from is_activated to is_verified
ALTER TABLE user 
CHANGE COLUMN is_activated is_verified BOOLEAN NOT NULL DEFAULT FALSE;

-- Set all users to not verified (false)
UPDATE user 
SET is_verified = FALSE;

-- Add index for better query performance
CREATE INDEX idx_user_is_verified ON user(is_verified);

-- Verify the changes
DESCRIBE user;
SELECT COUNT(*) as total_users, 
       SUM(CASE WHEN is_verified = TRUE THEN 1 ELSE 0 END) as verified_users,
       SUM(CASE WHEN is_verified = FALSE THEN 1 ELSE 0 END) as not_verified_users
FROM user;
