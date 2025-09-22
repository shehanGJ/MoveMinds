-- Create program content tables for detailed learning structure

-- Program Module Table
CREATE TABLE IF NOT EXISTS program_module (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INT NOT NULL DEFAULT 0,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fitness_program_id INT NOT NULL,
    FOREIGN KEY (fitness_program_id) REFERENCES fitness_program(id) ON DELETE CASCADE,
    INDEX idx_program_module_program (fitness_program_id),
    INDEX idx_program_module_order (fitness_program_id, order_index)
);

-- Program Lesson Table
CREATE TABLE IF NOT EXISTS program_lesson (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    content TEXT,
    video_url VARCHAR(500),
    duration_minutes INT,
    order_index INT NOT NULL DEFAULT 0,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    is_preview BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    program_module_id INT NOT NULL,
    FOREIGN KEY (program_module_id) REFERENCES program_module(id) ON DELETE CASCADE,
    INDEX idx_program_lesson_module (program_module_id),
    INDEX idx_program_lesson_order (program_module_id, order_index)
);

-- Program Resource Table
CREATE TABLE IF NOT EXISTS program_resource (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size_bytes BIGINT,
    order_index INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    program_lesson_id INT NOT NULL,
    FOREIGN KEY (program_lesson_id) REFERENCES program_lesson(id) ON DELETE CASCADE,
    INDEX idx_program_resource_lesson (program_lesson_id),
    INDEX idx_program_resource_order (program_lesson_id, order_index)
);

-- Add indexes for better performance
CREATE INDEX idx_program_module_published ON program_module(fitness_program_id, is_published, order_index);
CREATE INDEX idx_program_lesson_published ON program_lesson(program_module_id, is_published, order_index);
