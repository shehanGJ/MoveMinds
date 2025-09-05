SET FOREIGN_KEY_CHECKS=0;
TRUNCATE TABLE program_attribute;
TRUNCATE TABLE program_image;
TRUNCATE TABLE user_program;
TRUNCATE TABLE subscription;
TRUNCATE TABLE comment;
TRUNCATE TABLE message;
TRUNCATE TABLE activity;
TRUNCATE TABLE attribute_value;
TRUNCATE TABLE attribute;
TRUNCATE TABLE fitness_program;
TRUNCATE TABLE location;
TRUNCATE TABLE `user`;
TRUNCATE TABLE category;
TRUNCATE TABLE city;
TRUNCATE TABLE log;
SET FOREIGN_KEY_CHECKS=1;

-- Cities
INSERT INTO city (name) VALUES
('Panadura'),('Kandy'),('Galle'),('Jaffna'),('Negombo'),('Matara'),('Kurunegala'),('Trincomalee');

-- Categories
INSERT INTO category (name, description) VALUES
('Cardio','Cardiovascular exercises and training programs'),
('Strength Training','Weight lifting and muscle building programs'),
('Yoga','Yoga and flexibility training programs'),
('Pilates','Pilates and core strengthening programs'),
('CrossFit','High-intensity functional fitness programs'),
('Swimming','Swimming and water-based exercises'),
('Running','Running and endurance training programs'),
('Dance','Dance fitness and aerobic programs');

-- Attributes (use description column in current schema)
INSERT INTO attribute (name, description, category_id) VALUES
('Duration','30-60 minutes',1),
('Intensity','Moderate to High',1),
('Equipment','None required',1),
('Calories Burned','300-600 per session',1),
('Duration','45-90 minutes',2),
('Intensity','High',2),
('Equipment','Weights, Resistance Bands',2),
('Muscle Groups','Full Body',2),
('Duration','30-90 minutes',3),
('Intensity','Low to Moderate',3),
('Equipment','Yoga Mat',3),
('Focus','Flexibility and Mindfulness',3),
('Duration','30-60 minutes',4),
('Intensity','Moderate',4),
('Equipment','Pilates Mat, Optional Props',4),
('Focus','Core Strength',4);

-- Locations (current table has only name)
INSERT INTO location (name) VALUES
('Fitness Center Colombo'),
('Gym Kandy'),
('Sports Complex Galle'),
('Wellness Jaffna'),
('Aqua Park Negombo');

-- Users (role enum: USER/ADMIN/INSTRUCTOR)
INSERT INTO `user` (username, password, email, first_name, last_name, role, is_activated, city_id, biography) VALUES
('shehan', 'shehan@12', 'shehan@moveminds.lk', 'Shehan', 'Anujaya', 'ADMIN', 1, 1, 'System administrator for Fitness Tracker'),
('bhanuka', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'bhanuka@moveminds.lk', 'Bhanuka', 'Viraj', 'INSTRUCTOR', 1, 1, 'Certified personal trainer with 5 years of experience'),
('oshan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'oshan@moveminds.lk', 'Oshan', 'Avishka', 'INSTRUCTOR', 1, 2, 'Yoga and Pilates instructor'),
('vidura', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'vidura@moveminds.lk', 'Vidura', 'Priyadarshana', 'USER', 1, 1, 'Fitness enthusiast looking to improve my health'),
('hiruna', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'hiruna@moveminds.lk', 'Hiruna', 'Dissanayake', 'USER', 1, 3, 'Beginner in fitness, looking for guidance');

-- Programs (use subselects to resolve foreign keys)
INSERT INTO fitness_program (name, description, difficulty_level, duration, price, instructor_id, category_id, location_id, youtube_url) VALUES
('Morning Cardio Blast','High-energy cardio workout to start your day with energy','BEGINNER',30,15.00,(SELECT id FROM `user` WHERE username='bhanuka'),(SELECT id FROM category WHERE name='Cardio'),(SELECT id FROM location WHERE name='Fitness Center Colombo'),'https://youtube.com/watch?v=sample1'),
('Strength Training Basics','Learn proper form and technique for basic strength exercises','BEGINNER',45,25.00,(SELECT id FROM `user` WHERE username='bhanuka'),(SELECT id FROM category WHERE name='Strength Training'),(SELECT id FROM location WHERE name='Fitness Center Colombo'),'https://youtube.com/watch?v=sample2'),
('Yoga for Beginners','Gentle yoga flow perfect for beginners','BEGINNER',60,20.00,(SELECT id FROM `user` WHERE username='oshan'),(SELECT id FROM category WHERE name='Yoga'),(SELECT id FROM location WHERE name='Gym Kandy'),'https://youtube.com/watch?v=sample3'),
('Advanced CrossFit','High-intensity functional fitness for experienced athletes','ADVANCED',60,35.00,(SELECT id FROM `user` WHERE username='bhanuka'),(SELECT id FROM category WHERE name='CrossFit'),(SELECT id FROM location WHERE name='Fitness Center Colombo'),'https://youtube.com/watch?v=sample4'),
('Pilates Core Workout','Focus on core strength and stability','INTERMEDIATE',45,22.00,(SELECT id FROM `user` WHERE username='oshan'),(SELECT id FROM category WHERE name='Pilates'),(SELECT id FROM location WHERE name='Gym Kandy'),'https://youtube.com/watch?v=sample5'),
('Swimming Technique','Improve your swimming technique and endurance','INTERMEDIATE',50,30.00,(SELECT id FROM `user` WHERE username='bhanuka'),(SELECT id FROM category WHERE name='Swimming'),(SELECT id FROM location WHERE name='Aqua Park Negombo'),'https://youtube.com/watch?v=sample6');

-- Activities
INSERT INTO activity (activity_type, duration, intensity, result, log_date, user_id) VALUES
('Running',30,'Moderate',5.2,'2024-01-15',(SELECT id FROM `user` WHERE username='vidura')),
('Weight Training',45,'High',0.0,'2024-01-15',(SELECT id FROM `user` WHERE username='vidura')),
('Yoga',60,'Low',0.0,'2024-01-16',(SELECT id FROM `user` WHERE username='hiruna')),
('Swimming',40,'Moderate',1.5,'2024-01-16',(SELECT id FROM `user` WHERE username='vidura')),
('Cardio',25,'High',0.0,'2024-01-17',(SELECT id FROM `user` WHERE username='hiruna'));

-- Comments (set posted_at to now)
INSERT INTO comment (content, posted_at, user_id, fitness_program_id) VALUES
('Great workout! Really enjoyed the cardio session.', NOW(), (SELECT id FROM `user` WHERE username='vidura'), (SELECT id FROM fitness_program WHERE name='Morning Cardio Blast')),
('The instructor was very helpful with form corrections.', NOW(), (SELECT id FROM `user` WHERE username='hiruna'), (SELECT id FROM fitness_program WHERE name='Strength Training Basics')),
('Perfect for beginners like me. Highly recommended!', NOW(), (SELECT id FROM `user` WHERE username='vidura'), (SELECT id FROM fitness_program WHERE name='Yoga for Beginners')),
('Challenging but rewarding workout.', NOW(), (SELECT id FROM `user` WHERE username='hiruna'), (SELECT id FROM fitness_program WHERE name='Advanced CrossFit'));

-- Messages (sender/recipient, set sent_at)
INSERT INTO message (subject, content, sent_at, sender_id, recipient_id) VALUES
('Welcome to Fitness Tracker!','Welcome to our fitness community. Feel free to ask any questions!', NOW(), (SELECT id FROM `user` WHERE username='bhanuka'), (SELECT id FROM `user` WHERE username='vidura')),
('Program Recommendation','I think the Yoga for Beginners program would be perfect for you.', NOW(), (SELECT id FROM `user` WHERE username='oshan'), (SELECT id FROM `user` WHERE username='hiruna')),
('Training Schedule','Let me know when you would like to schedule your next training session.', NOW(), (SELECT id FROM `user` WHERE username='bhanuka'), (SELECT id FROM `user` WHERE username='vidura'));

-- Subscriptions
INSERT INTO subscription (user_id, category_id) VALUES
((SELECT id FROM `user` WHERE username='vidura'), (SELECT id FROM category WHERE name='Cardio')),
((SELECT id FROM `user` WHERE username='vidura'), (SELECT id FROM category WHERE name='Strength Training')),
((SELECT id FROM `user` WHERE username='hiruna'), (SELECT id FROM category WHERE name='Yoga')),
((SELECT id FROM `user` WHERE username='hiruna'), (SELECT id FROM category WHERE name='Pilates'));

-- User Programs (enrollments) - include required fields
INSERT INTO user_program (status, start_date, end_date, user_id, program_id) VALUES
('ACTIVE', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), (SELECT id FROM `user` WHERE username='vidura'), (SELECT id FROM fitness_program WHERE name='Morning Cardio Blast')),
('ACTIVE', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), (SELECT id FROM `user` WHERE username='vidura'), (SELECT id FROM fitness_program WHERE name='Strength Training Basics')),
('ACTIVE', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), (SELECT id FROM `user` WHERE username='hiruna'), (SELECT id FROM fitness_program WHERE name='Yoga for Beginners')),
('ACTIVE', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), (SELECT id FROM `user` WHERE username='hiruna'), (SELECT id FROM fitness_program WHERE name='Pilates Core Workout'));


