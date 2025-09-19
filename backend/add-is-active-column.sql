-- Migration script to add is_active column to fitness_program table
-- This script should be run to add the missing is_active column

-- Add the is_active column to fitness_program table
ALTER TABLE fitness_program 
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT FALSE;

-- Update all existing programs to be inactive by default
-- (This ensures existing programs require admin activation)
UPDATE fitness_program 
SET is_active = FALSE 
WHERE is_active IS NULL OR is_active = TRUE;

-- Add a comment to document the column
ALTER TABLE fitness_program 
MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT FALSE 
COMMENT 'Indicates if the program is active and available for enrollment. Defaults to FALSE (inactive) and requires admin activation.';
