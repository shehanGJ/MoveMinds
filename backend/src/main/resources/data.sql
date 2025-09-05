-- Insert initial cities
INSERT INTO city (name) VALUES 
('Panadura'),
('Kandy'),
('Galle'),
('Jaffna'),
('Negombo'),
('Matara'),
('Kurunegala'),
('Trincomalee');

-- Insert initial categories
INSERT INTO category (name, description) VALUES 
('Cardio', 'Cardiovascular exercises and training programs'),
('Strength Training', 'Weight lifting and muscle building programs'),
('Yoga', 'Yoga and flexibility training programs'),
('Pilates', 'Pilates and core strengthening programs'),
('CrossFit', 'High-intensity functional fitness programs'),
('Swimming', 'Swimming and water-based exercises'),
('Running', 'Running and endurance training programs'),
('Dance', 'Dance fitness and aerobic programs');

-- Insert initial attributes for categories
INSERT INTO attribute (name, value, category_id) VALUES 
-- Cardio attributes
('Duration', '30-60 minutes', 1),
('Intensity', 'Moderate to High', 1),
('Equipment', 'None required', 1),
('Calories Burned', '300-600 per session', 1),

-- Strength Training attributes
('Duration', '45-90 minutes', 2),
('Intensity', 'High', 2),
('Equipment', 'Weights, Resistance Bands', 2),
('Muscle Groups', 'Full Body', 2),

-- Yoga attributes
('Duration', '30-90 minutes', 3),
('Intensity', 'Low to Moderate', 3),
('Equipment', 'Yoga Mat', 3),
('Focus', 'Flexibility and Mindfulness', 3),

-- Pilates attributes
('Duration', '30-60 minutes', 4),
('Intensity', 'Moderate', 4),
('Equipment', 'Pilates Mat, Optional Props', 4),
('Focus', 'Core Strength', 4);

-- Insert sample locations
INSERT INTO location (name, address, city_id) VALUES 
('Fitness Center Sarajevo', 'Trg BiH 1, Sarajevo', 1),
('Gym Banja Luka', 'Kralja Petra I 15, Banja Luka', 2),
('Sports Complex Tuzla', 'Slatina bb, Tuzla', 3),
('Wellness Zenica', 'Kemala Kapetanovića 2, Zenica', 4),
('Aqua Park Mostar', 'Rondo bb, Mostar', 5);

-- Insert sample users (password is 'password' encoded with BCrypt)
-- Note: city_id references will be resolved after cities are inserted
INSERT INTO user (username, password, email, first_name, last_name, role, is_activated, city_id, biography) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@fitness.com', 'Admin', 'User', 'ADMIN', true, 1, 'System administrator for Fitness Tracker'),
('trainer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'trainer1@fitness.com', 'Marko', 'Petrović', 'TRAINER', true, 1, 'Certified personal trainer with 5 years of experience'),
('trainer2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'trainer2@fitness.com', 'Ana', 'Kovačević', 'TRAINER', true, 2, 'Yoga and Pilates instructor'),
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user1@fitness.com', 'Petar', 'Marković', 'USER', true, 1, 'Fitness enthusiast looking to improve my health'),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user2@fitness.com', 'Jelena', 'Nikolić', 'USER', true, 3, 'Beginner in fitness, looking for guidance');

-- Insert sample fitness programs
INSERT INTO fitness_program (name, description, difficulty_level, duration, price, instructor_id, category_id, location_id, youtube_url) VALUES 
('Morning Cardio Blast', 'High-energy cardio workout to start your day with energy', 'BEGINNER', 30, 15.00, 2, 1, 1, 'https://youtube.com/watch?v=sample1'),
('Strength Training Basics', 'Learn proper form and technique for basic strength exercises', 'BEGINNER', 45, 25.00, 2, 2, 1, 'https://youtube.com/watch?v=sample2'),
('Yoga for Beginners', 'Gentle yoga flow perfect for beginners', 'BEGINNER', 60, 20.00, 3, 3, 2, 'https://youtube.com/watch?v=sample3'),
('Advanced CrossFit', 'High-intensity functional fitness for experienced athletes', 'ADVANCED', 60, 35.00, 2, 5, 1, 'https://youtube.com/watch?v=sample4'),
('Pilates Core Workout', 'Focus on core strength and stability', 'INTERMEDIATE', 45, 22.00, 3, 4, 2, 'https://youtube.com/watch?v=sample5'),
('Swimming Technique', 'Improve your swimming technique and endurance', 'INTERMEDIATE', 50, 30.00, 2, 6, 5, 'https://youtube.com/watch?v=sample6');

-- Insert sample activities
INSERT INTO activity (activity_type, duration, intensity, result, log_date, user_id) VALUES 
('Running', 30, 'Moderate', 5.2, '2024-01-15', 4),
('Weight Training', 45, 'High', 0.0, '2024-01-15', 4),
('Yoga', 60, 'Low', 0.0, '2024-01-16', 5),
('Swimming', 40, 'Moderate', 1.5, '2024-01-16', 4),
('Cardio', 25, 'High', 0.0, '2024-01-17', 5);

-- Insert sample comments
INSERT INTO comment (content, user_id, fitness_program_id) VALUES 
('Great workout! Really enjoyed the cardio session.', 4, 1),
('The instructor was very helpful with form corrections.', 5, 2),
('Perfect for beginners like me. Highly recommended!', 4, 3),
('Challenging but rewarding workout.', 5, 4);

-- Insert sample messages
INSERT INTO message (subject, content, from_user_id, to_user_id) VALUES 
('Welcome to Fitness Tracker!', 'Welcome to our fitness community. Feel free to ask any questions!', 2, 4),
('Program Recommendation', 'I think the Yoga for Beginners program would be perfect for you.', 3, 5),
('Training Schedule', 'Let me know when you would like to schedule your next training session.', 2, 4);

-- Insert sample subscriptions
INSERT INTO subscription (user_id, category_id) VALUES 
(4, 1), -- user1 subscribed to Cardio
(4, 2), -- user1 subscribed to Strength Training
(5, 3), -- user2 subscribed to Yoga
(5, 4); -- user2 subscribed to Pilates

-- Insert sample user programs (users enrolled in programs)
INSERT INTO user_program (user_id, program_id) VALUES 
(4, 1), -- user1 enrolled in Morning Cardio Blast
(4, 2), -- user1 enrolled in Strength Training Basics
(5, 3), -- user2 enrolled in Yoga for Beginners
(5, 5); -- user2 enrolled in Pilates Core Workout
CREATE TABLE activity
(
    id            INT AUTO_INCREMENT NOT NULL,
    activity_type VARCHAR(255)       NOT NULL,
    duration      INT                NOT NULL,
    intensity     VARCHAR(255)       NOT NULL,
    result        DECIMAL            NOT NULL,
    log_date      date               NOT NULL,
    user_id       INT                NOT NULL,
    CONSTRAINT pk_activity PRIMARY KEY (id)
);

CREATE TABLE attribute
(
    id            INT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255)       NOT NULL,
    `description` TEXT               NULL,
    category_id   INT                NOT NULL,
    CONSTRAINT pk_attribute PRIMARY KEY (id)
);

CREATE TABLE attribute_value
(
    id           INT AUTO_INCREMENT NOT NULL,
    name         VARCHAR(255)       NOT NULL,
    attribute_id INT                NOT NULL,
    CONSTRAINT pk_attribute_value PRIMARY KEY (id)
);

CREATE TABLE category
(
    id            INT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255)       NOT NULL,
    `description` TEXT               NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE city
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_city PRIMARY KEY (id)
);

CREATE TABLE comment
(
    id                 INT AUTO_INCREMENT NOT NULL,
    content            TEXT               NOT NULL,
    posted_at          datetime           NOT NULL,
    user_id            INT                NOT NULL,
    fitness_program_id INT                NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

CREATE TABLE fitness_program
(
    id               INT AUTO_INCREMENT NOT NULL,
    name             VARCHAR(255)       NOT NULL,
    `description`    TEXT               NOT NULL,
    difficulty_level VARCHAR(255)       NOT NULL,
    duration         INT                NOT NULL,
    price            DECIMAL(2)         NOT NULL,
    created_at       datetime           NULL,
    youtube_url      VARCHAR(255)       NULL,
    category_id      INT                NULL,
    instructor_id    INT                NOT NULL,
    location_id      INT                NULL,
    CONSTRAINT pk_fitness_program PRIMARY KEY (id)
);

CREATE TABLE location
(
    id   INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE log
(
    id        INT AUTO_INCREMENT NOT NULL,
    user      VARCHAR(255)       NULL,
    action    VARCHAR(255)       NULL,
    timestamp datetime           NULL,
    CONSTRAINT pk_log PRIMARY KEY (id)
);

CREATE TABLE message
(
    id           INT AUTO_INCREMENT NOT NULL,
    subject      VARCHAR(255)       NULL,
    content      TEXT               NOT NULL,
    sent_at      datetime           NOT NULL,
    read_at      datetime           NULL,
    sender_id    INT                NOT NULL,
    recipient_id INT                NOT NULL,
    CONSTRAINT pk_message PRIMARY KEY (id)
);

CREATE TABLE program_attribute
(
    id                 INT AUTO_INCREMENT NOT NULL,
    fitness_program_id INT                NOT NULL,
    attribute_value_id INT                NOT NULL,
    CONSTRAINT pk_program_attribute PRIMARY KEY (id)
);

CREATE TABLE program_image
(
    id                 INT AUTO_INCREMENT NOT NULL,
    image_url          VARCHAR(255)       NOT NULL,
    fitness_program_id INT                NOT NULL,
    CONSTRAINT pk_program_image PRIMARY KEY (id)
);

CREATE TABLE subscription
(
    id          INT AUTO_INCREMENT NOT NULL,
    user_id     INT                NOT NULL,
    category_id INT                NOT NULL,
    CONSTRAINT pk_subscription PRIMARY KEY (id)
);

CREATE TABLE user
(
    id           INT AUTO_INCREMENT NOT NULL,
    username     VARCHAR(255)       NOT NULL,
    password     VARCHAR(255)       NOT NULL,
    email        VARCHAR(255)       NOT NULL,
    first_name   VARCHAR(255)       NULL,
    last_name    VARCHAR(255)       NULL,
    `role`       VARCHAR(255)       NOT NULL,
    is_activated BIT(1)             NOT NULL,
    avatar_url   VARCHAR(255)       NULL,
    biography    TEXT               NULL,
    city_id      INT                NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE user_program
(
    id         INT AUTO_INCREMENT NOT NULL,
    status     VARCHAR(255)       NOT NULL,
    start_date date               NOT NULL,
    end_date   date               NOT NULL,
    user_id    INT                NOT NULL,
    program_id INT                NOT NULL,
    CONSTRAINT pk_user_program PRIMARY KEY (id)
);

ALTER TABLE activity
    ADD CONSTRAINT FK_ACTIVITY_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE attribute
    ADD CONSTRAINT FK_ATTRIBUTE_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE attribute_value
    ADD CONSTRAINT FK_ATTRIBUTE_VALUE_ON_ATTRIBUTE FOREIGN KEY (attribute_id) REFERENCES attribute (id);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_FITNESS_PROGRAM FOREIGN KEY (fitness_program_id) REFERENCES fitness_program (id);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE fitness_program
    ADD CONSTRAINT FK_FITNESS_PROGRAM_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE fitness_program
    ADD CONSTRAINT FK_FITNESS_PROGRAM_ON_INSTRUCTOR FOREIGN KEY (instructor_id) REFERENCES user (id);

ALTER TABLE fitness_program
    ADD CONSTRAINT FK_FITNESS_PROGRAM_ON_LOCATION FOREIGN KEY (location_id) REFERENCES location (id);

ALTER TABLE message
    ADD CONSTRAINT FK_MESSAGE_ON_RECIPIENT FOREIGN KEY (recipient_id) REFERENCES user (id);

ALTER TABLE message
    ADD CONSTRAINT FK_MESSAGE_ON_SENDER FOREIGN KEY (sender_id) REFERENCES user (id);

ALTER TABLE program_attribute
    ADD CONSTRAINT FK_PROGRAM_ATTRIBUTE_ON_ATTRIBUTE_VALUE FOREIGN KEY (attribute_value_id) REFERENCES attribute_value (id);

ALTER TABLE program_attribute
    ADD CONSTRAINT FK_PROGRAM_ATTRIBUTE_ON_FITNESS_PROGRAM FOREIGN KEY (fitness_program_id) REFERENCES fitness_program (id);

ALTER TABLE program_image
    ADD CONSTRAINT FK_PROGRAM_IMAGE_ON_FITNESS_PROGRAM FOREIGN KEY (fitness_program_id) REFERENCES fitness_program (id);

ALTER TABLE subscription
    ADD CONSTRAINT FK_SUBSCRIPTION_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE subscription
    ADD CONSTRAINT FK_SUBSCRIPTION_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

ALTER TABLE user
    ADD CONSTRAINT FK_USER_ON_CITY FOREIGN KEY (city_id) REFERENCES city (id);

ALTER TABLE user_program
    ADD CONSTRAINT FK_USER_PROGRAM_ON_PROGRAM FOREIGN KEY (program_id) REFERENCES fitness_program (id);

ALTER TABLE user_program
    ADD CONSTRAINT FK_USER_PROGRAM_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);