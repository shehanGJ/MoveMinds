-- Create user_progress table for tracking individual lesson progress
CREATE TABLE user_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    program_id INT NOT NULL,
    lesson_id INT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at TIMESTAMP NULL,
    watch_time_seconds INT DEFAULT 0,
    last_watched_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES fitness_program(id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_id) REFERENCES program_lesson(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_user_lesson (user_id, lesson_id),
    INDEX idx_user_program (user_id, program_id),
    INDEX idx_lesson (lesson_id),
    INDEX idx_completed (is_completed),
    INDEX idx_created_at (created_at)
);

-- Create user_program_progress table for tracking overall program progress
CREATE TABLE user_program_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    program_id INT NOT NULL,
    total_lessons INT NOT NULL DEFAULT 0,
    completed_lessons INT NOT NULL DEFAULT 0,
    progress_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    total_watch_time_seconds INT DEFAULT 0,
    last_accessed_at TIMESTAMP NULL,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    is_program_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (program_id) REFERENCES fitness_program(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_user_program (user_id, program_id),
    INDEX idx_user (user_id),
    INDEX idx_program (program_id),
    INDEX idx_completed (is_program_completed),
    INDEX idx_progress (progress_percentage),
    INDEX idx_started_at (started_at)
);
