package com.java.moveminds.services.impl;

import com.java.moveminds.dto.request.MarkLessonCompleteRequest;
import com.java.moveminds.dto.response.*;
import com.java.moveminds.entities.*;
import com.java.moveminds.repositories.*;
import com.java.moveminds.services.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProgressServiceImpl implements UserProgressService {
    
    private final UserProgressEntityRepository userProgressRepository;
    private final UserProgramProgressEntityRepository userProgramProgressRepository;
    private final UserEntityRepository userRepository;
    private final FitnessProgramEntityRepository fitnessProgramRepository;
    private final ProgramLessonEntityRepository lessonRepository;
    private final ProgramModuleEntityRepository moduleRepository;
    
    @Override
    public UserProgressResponse markLessonComplete(MarkLessonCompleteRequest request) {
        log.info("Marking lesson {} as complete for current user", request.getLessonId());
        
        UserEntity currentUser = getCurrentUser();
        ProgramLessonEntity lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        // Find or create user progress for this lesson
        UserProgressEntity progress = userProgressRepository.findByUserAndLesson(currentUser, lesson)
                .orElse(new UserProgressEntity());
        
        if (progress.getId() == null) {
            // New progress entry
            progress.setUser(currentUser);
            progress.setLesson(lesson);
            progress.setFitnessProgram(lesson.getProgramModule().getFitnessProgram());
        }
        
        // Mark as completed
        progress.markAsCompleted();
        if (request.getWatchTimeSeconds() != null && request.getWatchTimeSeconds() > 0) {
            progress.updateWatchTime(request.getWatchTimeSeconds());
        }
        
        UserProgressEntity savedProgress = userProgressRepository.save(progress);
        
        // Update program progress
        updateProgramProgress(currentUser, lesson.getProgramModule().getFitnessProgram());
        
        log.info("Successfully marked lesson {} as complete for user {}", request.getLessonId(), currentUser.getId());
        return mapToUserProgressResponse(savedProgress);
    }
    
    @Override
    public UserProgressResponse markLessonIncomplete(Integer lessonId) {
        log.info("Marking lesson {} as incomplete for current user", lessonId);
        
        UserEntity currentUser = getCurrentUser();
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        // Find existing progress for this lesson
        Optional<UserProgressEntity> existingProgress = userProgressRepository.findByUserAndLesson(currentUser, lesson);
        
        if (existingProgress.isPresent()) {
            UserProgressEntity progress = existingProgress.get();
            progress.setIsCompleted(false);
            progress.setCompletedAt(null);
            progress.setLastWatchedAt(LocalDateTime.now());
            
            UserProgressEntity savedProgress = userProgressRepository.save(progress);
            
            // Update program progress
            updateProgramProgress(currentUser, lesson.getProgramModule().getFitnessProgram());
            
            log.info("Successfully marked lesson {} as incomplete for user {}", lessonId, currentUser.getId());
            return mapToUserProgressResponse(savedProgress);
        } else {
            // No existing progress, return default incomplete response
            UserProgressResponse response = new UserProgressResponse();
            response.setUserId(currentUser.getId());
            response.setProgramId(lesson.getProgramModule().getFitnessProgram().getId());
            response.setLessonId(lessonId);
            response.setLessonTitle(lesson.getTitle());
            response.setModuleTitle(lesson.getProgramModule().getTitle());
            response.setIsCompleted(false);
            response.setWatchTimeSeconds(0);
            return response;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ProgramLearningProgressResponse getProgramProgress(Integer programId) {
        log.info("Getting program progress for program {} and current user", programId);
        
        UserEntity currentUser = getCurrentUser();
        FitnessProgramEntity program = fitnessProgramRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));
        
        // Get all lessons in the program
        List<ProgramModuleEntity> modules = moduleRepository.findPublishedByProgramIdOrderByOrderIndex(programId);
        List<ProgramLessonEntity> allLessons = modules.stream()
                .flatMap(module -> module.getLessons().stream())
                .filter(lesson -> lesson.getIsPublished() != null && lesson.getIsPublished())
                .collect(Collectors.toList());
        
        // Get user progress for all lessons
        List<UserProgressEntity> userProgress = userProgressRepository.findByUserAndFitnessProgram(currentUser, program);
        
        // Create lesson progress responses
        List<ProgramLearningProgressResponse.LessonProgressResponse> lessonProgress = allLessons.stream()
                .map(lesson -> {
                    Optional<UserProgressEntity> progress = userProgress.stream()
                            .filter(p -> p.getLesson().getId().equals(lesson.getId()))
                            .findFirst();
                    
                    ProgramLearningProgressResponse.LessonProgressResponse response = new ProgramLearningProgressResponse.LessonProgressResponse();
                    response.setLessonId(lesson.getId());
                    response.setLessonTitle(lesson.getTitle());
                    response.setModuleTitle(lesson.getProgramModule().getTitle());
                    response.setIsCompleted(progress.map(UserProgressEntity::getIsCompleted).orElse(false));
                    response.setWatchTimeSeconds(progress.map(UserProgressEntity::getWatchTimeSeconds).orElse(0));
                    response.setDurationMinutes(lesson.getDurationMinutes());
                    response.setIsPreview(lesson.getIsPreview());
                    return response;
                })
                .collect(Collectors.toList());
        
        // Calculate overall progress
        int totalLessons = allLessons.size();
        int completedLessons = (int) userProgress.stream()
                .filter(UserProgressEntity::getIsCompleted)
                .count();
        
        double progressPercentage = totalLessons > 0 ? (double) completedLessons / totalLessons * 100.0 : 0.0;
        
        // Get program progress
        UserProgramProgressEntity programProgress = userProgramProgressRepository
                .findByUserAndFitnessProgram(currentUser, program)
                .orElse(null);
        
        ProgramLearningProgressResponse response = new ProgramLearningProgressResponse();
        response.setProgramId(programId);
        response.setProgramName(program.getName());
        response.setTotalLessons(totalLessons);
        response.setCompletedLessons(completedLessons);
        response.setProgressPercentage(progressPercentage);
        response.setTotalWatchTimeSeconds(programProgress != null ? programProgress.getTotalWatchTimeSeconds() : 0);
        response.setIsProgramCompleted(completedLessons == totalLessons && totalLessons > 0);
        response.setLessonProgress(lessonProgress);
        
        log.info("Retrieved program progress: {}/{} lessons completed ({}%)", 
                completedLessons, totalLessons, String.format("%.1f", progressPercentage));
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserProgramProgressResponse> getAllUserProgress() {
        UserEntity currentUser = getCurrentUser();
        List<UserProgramProgressEntity> programProgress = userProgramProgressRepository.findByUser(currentUser);
        
        return programProgress.stream()
                .map(this::mapToUserProgramProgressResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserProgressResponse getLessonProgress(Integer lessonId) {
        UserEntity currentUser = getCurrentUser();
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        UserProgressEntity progress = userProgressRepository.findByUserAndLesson(currentUser, lesson)
                .orElse(null);
        
        if (progress == null) {
            // Return default progress (not completed)
            UserProgressResponse response = new UserProgressResponse();
            response.setUserId(currentUser.getId());
            response.setProgramId(lesson.getProgramModule().getFitnessProgram().getId());
            response.setLessonId(lessonId);
            response.setLessonTitle(lesson.getTitle());
            response.setModuleTitle(lesson.getProgramModule().getTitle());
            response.setIsCompleted(false);
            response.setWatchTimeSeconds(0);
            return response;
        }
        
        return mapToUserProgressResponse(progress);
    }
    
    @Override
    public UserProgressResponse updateWatchTime(Integer lessonId, Integer watchTimeSeconds) {
        UserEntity currentUser = getCurrentUser();
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        UserProgressEntity progress = userProgressRepository.findByUserAndLesson(currentUser, lesson)
                .orElse(new UserProgressEntity());
        
        if (progress.getId() == null) {
            progress.setUser(currentUser);
            progress.setLesson(lesson);
            progress.setFitnessProgram(lesson.getProgramModule().getFitnessProgram());
        }
        
        progress.updateWatchTime(watchTimeSeconds);
        UserProgressEntity savedProgress = userProgressRepository.save(progress);
        
        // Update program progress
        updateProgramProgress(currentUser, lesson.getProgramModule().getFitnessProgram());
        
        return mapToUserProgressResponse(savedProgress);
    }
    
    @Override
    public void initializeProgramProgress(Integer programId) {
        UserEntity currentUser = getCurrentUser();
        FitnessProgramEntity program = fitnessProgramRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));
        
        // Check if program progress already exists
        Optional<UserProgramProgressEntity> existingProgress = userProgramProgressRepository
                .findByUserAndFitnessProgram(currentUser, program);
        
        if (existingProgress.isEmpty()) {
            // Create new program progress
            UserProgramProgressEntity programProgress = new UserProgramProgressEntity();
            programProgress.setUser(currentUser);
            programProgress.setFitnessProgram(program);
            programProgress.setStartedAt(LocalDateTime.now());
            programProgress.setLastAccessedAt(LocalDateTime.now());
            
            // Calculate total lessons
            List<ProgramModuleEntity> modules = moduleRepository.findPublishedByProgramIdOrderByOrderIndex(programId);
            int totalLessons = modules.stream()
                    .mapToInt(module -> module.getLessons() != null ? 
                            (int) module.getLessons().stream()
                                    .filter(lesson -> lesson.getIsPublished() != null && lesson.getIsPublished())
                                    .count() : 0)
                    .sum();
            
            programProgress.updateProgress(totalLessons, 0);
            userProgramProgressRepository.save(programProgress);
            
            log.info("Initialized program progress for user {} in program {}", currentUser.getId(), programId);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserProgressStatsResponse getUserProgressStats() {
        UserEntity currentUser = getCurrentUser();
        
        List<UserProgramProgressEntity> allProgress = userProgramProgressRepository.findByUser(currentUser);
        
        UserProgressStatsResponse stats = new UserProgressStatsResponse();
        stats.setTotalProgramsEnrolled(allProgress.size());
        stats.setCompletedPrograms((int) allProgress.stream()
                .filter(UserProgramProgressEntity::getIsProgramCompleted)
                .count());
        stats.setInProgressPrograms((int) allProgress.stream()
                .filter(p -> !p.getIsProgramCompleted() && p.getCompletedLessons() > 0)
                .count());
        
        if (!allProgress.isEmpty()) {
            stats.setAverageProgressPercentage(allProgress.stream()
                    .mapToDouble(UserProgramProgressEntity::getProgressPercentage)
                    .average()
                    .orElse(0.0));
        } else {
            stats.setAverageProgressPercentage(0.0);
        }
        
        stats.setTotalLessonsCompleted(allProgress.stream()
                .mapToInt(UserProgramProgressEntity::getCompletedLessons)
                .sum());
        
        stats.setTotalWatchTimeHours(allProgress.stream()
                .mapToInt(p -> p.getTotalWatchTimeSeconds() != null ? p.getTotalWatchTimeSeconds() : 0)
                .sum() / 3600);
        
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Boolean isLessonCompleted(Integer lessonId) {
        UserEntity currentUser = getCurrentUser();
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        return userProgressRepository.isLessonCompletedByUser(currentUser, lesson);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserProgressResponse> getStudentProgress(Integer programId, Integer userId) {
        // This would be implemented for instructor view
        // For now, return empty list
        return List.of();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserProgressResponse> getAllStudentsProgress(Integer programId) {
        // This would be implemented for instructor view
        // For now, return empty list
        return List.of();
    }
    
    private void updateProgramProgress(UserEntity user, FitnessProgramEntity program) {
        UserProgramProgressEntity programProgress = userProgramProgressRepository
                .findByUserAndFitnessProgram(user, program)
                .orElse(new UserProgramProgressEntity());
        
        if (programProgress.getId() == null) {
            programProgress.setUser(user);
            programProgress.setFitnessProgram(program);
            programProgress.setStartedAt(LocalDateTime.now());
        }
        
        // Calculate total and completed lessons
        List<ProgramModuleEntity> modules = moduleRepository.findPublishedByProgramIdOrderByOrderIndex(program.getId());
        int totalLessons = modules.stream()
                .mapToInt(module -> module.getLessons() != null ? 
                        (int) module.getLessons().stream()
                                .filter(lesson -> lesson.getIsPublished() != null && lesson.getIsPublished())
                                .count() : 0)
                .sum();
        
        Long completedLessons = userProgressRepository.countCompletedLessonsByUserAndProgram(user, program);
        
        programProgress.updateProgress(totalLessons, completedLessons.intValue());
        programProgress.setLastAccessedAt(LocalDateTime.now());
        
        userProgramProgressRepository.save(programProgress);
    }
    
    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private UserProgressResponse mapToUserProgressResponse(UserProgressEntity entity) {
        UserProgressResponse response = new UserProgressResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setProgramId(entity.getFitnessProgram().getId());
        response.setLessonId(entity.getLesson().getId());
        response.setLessonTitle(entity.getLesson().getTitle());
        response.setModuleTitle(entity.getLesson().getProgramModule().getTitle());
        response.setIsCompleted(entity.getIsCompleted());
        response.setCompletedAt(entity.getCompletedAt());
        response.setWatchTimeSeconds(entity.getWatchTimeSeconds());
        response.setLastWatchedAt(entity.getLastWatchedAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
    
    private UserProgramProgressResponse mapToUserProgramProgressResponse(UserProgramProgressEntity entity) {
        UserProgramProgressResponse response = new UserProgramProgressResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setProgramId(entity.getFitnessProgram().getId());
        response.setProgramName(entity.getFitnessProgram().getName());
        response.setTotalLessons(entity.getTotalLessons());
        response.setCompletedLessons(entity.getCompletedLessons());
        response.setProgressPercentage(entity.getProgressPercentage());
        response.setTotalWatchTimeSeconds(entity.getTotalWatchTimeSeconds());
        response.setLastAccessedAt(entity.getLastAccessedAt());
        response.setStartedAt(entity.getStartedAt());
        response.setCompletedAt(entity.getCompletedAt());
        response.setIsProgramCompleted(entity.getIsProgramCompleted());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
