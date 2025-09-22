package com.java.moveminds.services.impl;

import com.java.moveminds.dto.request.ProgramLessonRequest;
import com.java.moveminds.dto.request.ProgramModuleRequest;
import com.java.moveminds.dto.request.ProgramResourceRequest;
import com.java.moveminds.dto.response.ProgramLearningContentResponse;
import com.java.moveminds.dto.response.ProgramLessonResponse;
import com.java.moveminds.dto.response.ProgramModuleResponse;
import com.java.moveminds.dto.response.ProgramResourceResponse;
import com.java.moveminds.entities.*;
import com.java.moveminds.enums.Roles;
import com.java.moveminds.exceptions.UnauthorizedAccessException;
import com.java.moveminds.repositories.*;
import com.java.moveminds.services.FileStorageService;
import com.java.moveminds.services.ProgramContentManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgramContentManagementServiceImpl implements ProgramContentManagementService {
    
    private final FitnessProgramEntityRepository programRepository;
    private final ProgramModuleEntityRepository moduleRepository;
    private final ProgramLessonEntityRepository lessonRepository;
    private final ProgramResourceEntityRepository resourceRepository;
    private final UserEntityRepository userRepository;
    private final UserProgramEntityRepository userProgramRepository;
    private final FileStorageService fileStorageService;
    
    @Override
    @Transactional(readOnly = true)
    public ProgramLearningContentResponse getProgramLearningContent(Integer programId, Principal principal) {
        log.info("Fetching program learning content for program ID: {}", programId);
        
        FitnessProgramEntity program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found with ID: " + programId));
        log.info("Found program: {} (name: {})", program.getId(), program.getName());
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        log.info("Found user: {} (role: {})", user.getUsername(), user.getRole());
        
        // Check if user has access to this program
        if (!hasAccessToProgram(user, program)) {
            log.warn("Access denied for user: {} to program: {}", user.getUsername(), program.getName());
            throw new UnauthorizedAccessException("You don't have access to this program");
        }
        log.info("Access granted for user: {} to program: {}", user.getUsername(), program.getName());
        
        List<ProgramModuleEntity> modules;
        try {
            log.info("Fetching published modules for program ID: {}", programId);
            modules = moduleRepository.findPublishedByProgramIdOrderByOrderIndex(programId);
            log.info("Found {} published modules", modules != null ? modules.size() : 0);
            
            // Fetch resources for all lessons separately to avoid MultipleBagFetchException
            if (modules != null && !modules.isEmpty()) {
                List<Integer> lessonIds = modules.stream()
                        .filter(module -> module.getLessons() != null)
                        .flatMap(module -> module.getLessons().stream())
                        .map(ProgramLessonEntity::getId)
                        .collect(Collectors.toList());
                
                if (!lessonIds.isEmpty()) {
                    log.info("Fetching resources for {} lessons", lessonIds.size());
                    List<ProgramLessonEntity> lessonsWithResources = lessonRepository.findByIdsWithResources(lessonIds);
                    
                    // Update the lessons in modules with the fetched resources
                    modules.forEach(module -> {
                        if (module.getLessons() != null) {
                            module.getLessons().forEach(lesson -> {
                                lessonsWithResources.stream()
                                        .filter(lessonWithResources -> lessonWithResources.getId().equals(lesson.getId()))
                                        .findFirst()
                                        .ifPresent(lessonWithResources -> {
                                            lesson.setResources(lessonWithResources.getResources());
                                        });
                            });
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch published modules, trying fallback query: {}", e.getMessage(), e);
            // Fallback to simple query without eager loading
            modules = moduleRepository.findByProgramIdOrderByOrderIndex(programId)
                    .stream()
                    .filter(module -> module.getIsPublished() != null && module.getIsPublished())
                    .collect(Collectors.toList());
            log.info("Fallback query found {} published modules", modules.size());
        }
        
        // Calculate progress for enrolled users
        Integer completedLessons = 0;
        Double progressPercentage = 0.0;
        
        if (user.getRole() == Roles.USER) {
            boolean isEnrolled = userProgramRepository.existsByUserByUserIdAndFitnessProgramByProgramId(user, program);
            if (isEnrolled) {
                // TODO: Implement lesson completion tracking
                // For now, we'll use a placeholder
                completedLessons = 0;
                int totalLessons = modules != null ? modules.stream()
                        .mapToInt(module -> module.getLessons() != null ? module.getLessons().size() : 0)
                        .sum() : 0;
                progressPercentage = totalLessons > 0 ? (double) completedLessons / totalLessons * 100 : 0.0;
            }
        }
        
        int totalLessons = modules != null ? modules.stream()
                .mapToInt(module -> module.getLessons() != null ? module.getLessons().size() : 0)
                .sum() : 0;
        
        int totalDurationMinutes = modules != null ? modules.stream()
                .filter(module -> module.getLessons() != null)
                .flatMap(module -> module.getLessons().stream())
                .mapToInt(lesson -> lesson.getDurationMinutes() != null ? lesson.getDurationMinutes() : 0)
                .sum() : 0;
        
        log.info("Building response for program: {} with {} modules, {} total lessons", 
                program.getName(), modules != null ? modules.size() : 0, totalLessons);
        
        try {
            ProgramLearningContentResponse response = ProgramLearningContentResponse.builder()
                    .id(program.getId())
                    .name(program.getName())
                    .description(program.getDescription())
                    .difficultyLevel(program.getDifficultyLevel().toString())
                    .duration(program.getDuration())
                    .price(program.getPrice())
                    .instructorName(program.getUser().getFirstName() + " " + program.getUser().getLastName())
                    .instructorAvatarUrl(program.getUser().getAvatarUrl())
                    .categoryName(program.getCategory() != null ? program.getCategory().getName() : null)
                    .locationName(program.getLocation() != null ? program.getLocation().getName() : null)
                    .createdAt(program.getCreatedAt())
                    .modules(modules != null ? modules.stream().map(this::mapToModuleResponse).collect(Collectors.toList()) : List.of())
                    .totalLessons(totalLessons)
                    .totalDurationMinutes(totalDurationMinutes)
                    .completedLessons(completedLessons)
                    .progressPercentage(progressPercentage)
                    .build();
            
            log.info("Successfully built response for program: {}", program.getName());
            return response;
        } catch (Exception e) {
            log.error("Error building response for program: {} - {}", program.getName(), e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public ProgramModuleResponse createModule(Integer programId, ProgramModuleRequest request, Principal principal) {
        log.info("Creating module for program ID: {}", programId);
        
        FitnessProgramEntity program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found with ID: " + programId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, program)) {
            throw new UnauthorizedAccessException("You don't have permission to modify this program");
        }
        
        ProgramModuleEntity module = new ProgramModuleEntity();
        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        module.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : getNextModuleOrder(programId));
        module.setIsPublished(request.getIsPublished() != null ? request.getIsPublished() : false);
        module.setFitnessProgram(program);
        
        module = moduleRepository.save(module);
        
        return mapToModuleResponse(module);
    }
    
    @Override
    public ProgramModuleResponse updateModule(Integer moduleId, ProgramModuleRequest request, Principal principal) {
        log.info("Updating module ID: {}", moduleId);
        
        ProgramModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + moduleId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, module.getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to modify this module");
        }
        
        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        if (request.getOrderIndex() != null) {
            module.setOrderIndex(request.getOrderIndex());
        }
        if (request.getIsPublished() != null) {
            module.setIsPublished(request.getIsPublished());
        }
        
        module = moduleRepository.save(module);
        
        return mapToModuleResponse(module);
    }
    
    @Override
    public void deleteModule(Integer moduleId, Principal principal) {
        log.info("Deleting module ID: {}", moduleId);
        
        ProgramModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + moduleId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, module.getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to delete this module");
        }
        
        moduleRepository.delete(module);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProgramModuleResponse> getProgramModules(Integer programId, Principal principal) {
        log.info("Fetching modules for program ID: {}", programId);
        
        FitnessProgramEntity program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found with ID: " + programId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, program)) {
            throw new UnauthorizedAccessException("You don't have permission to view this program's modules");
        }
        
        List<ProgramModuleEntity> modules = moduleRepository.findByProgramIdOrderByOrderIndex(programId);
        
        return modules.stream()
                .map(this::mapToModuleResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProgramLessonResponse createLesson(Integer moduleId, ProgramLessonRequest request, Principal principal) {
        log.info("Creating lesson for module ID: {}", moduleId);
        
        ProgramModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + moduleId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, module.getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to modify this module");
        }
        
        ProgramLessonEntity lesson = new ProgramLessonEntity();
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDurationMinutes(request.getDurationMinutes());
        lesson.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : getNextLessonOrder(moduleId));
        lesson.setIsPublished(request.getIsPublished() != null ? request.getIsPublished() : false);
        lesson.setIsPreview(request.getIsPreview() != null ? request.getIsPreview() : false);
        lesson.setProgramModule(module);
        
        lesson = lessonRepository.save(lesson);
        
        return mapToLessonResponse(lesson);
    }
    
    @Override
    public ProgramLessonResponse updateLesson(Integer lessonId, ProgramLessonRequest request, Principal principal) {
        log.info("Updating lesson ID: {}", lessonId);
        
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, lesson.getProgramModule().getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to modify this lesson");
        }
        
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDurationMinutes(request.getDurationMinutes());
        if (request.getOrderIndex() != null) {
            lesson.setOrderIndex(request.getOrderIndex());
        }
        if (request.getIsPublished() != null) {
            lesson.setIsPublished(request.getIsPublished());
        }
        if (request.getIsPreview() != null) {
            lesson.setIsPreview(request.getIsPreview());
        }
        
        lesson = lessonRepository.save(lesson);
        
        return mapToLessonResponse(lesson);
    }
    
    @Override
    public void deleteLesson(Integer lessonId, Principal principal) {
        log.info("Deleting lesson ID: {}", lessonId);
        
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, lesson.getProgramModule().getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to delete this lesson");
        }
        
        lessonRepository.delete(lesson);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProgramLessonResponse> getModuleLessons(Integer moduleId, Principal principal) {
        log.info("Fetching lessons for module ID: {}", moduleId);
        
        ProgramModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + moduleId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, module.getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to view this module's lessons");
        }
        
        List<ProgramLessonEntity> lessons = lessonRepository.findByModuleIdOrderByOrderIndex(moduleId);
        
        return lessons.stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ProgramResourceResponse createResource(Integer lessonId, ProgramResourceRequest request, MultipartFile file, Principal principal) {
        log.info("Creating resource for lesson ID: {}", lessonId);
        
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, lesson.getProgramModule().getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to modify this lesson");
        }
        
        String fileUrl = request.getFileUrl();
        String fileType = request.getFileType();
        Long fileSizeBytes = request.getFileSizeBytes();
        
        // Handle file upload if provided
        if (file != null && !file.isEmpty()) {
            try {
                String filename = fileStorageService.storeFile(file, "resources");
                fileUrl = fileStorageService.getFileUrl(filename, "resources");
                fileType = file.getContentType() != null ? file.getContentType() : file.getOriginalFilename();
                fileSizeBytes = file.getSize();
                log.info("File uploaded successfully: {}", filename);
            } catch (IOException e) {
                log.error("Error uploading file: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to upload file: " + e.getMessage());
            }
        }
        
        ProgramResourceEntity resource = new ProgramResourceEntity();
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setFileUrl(fileUrl);
        resource.setFileType(fileType);
        resource.setFileSizeBytes(fileSizeBytes);
        resource.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : getNextResourceOrder(lessonId));
        resource.setProgramLesson(lesson);
        
        resource = resourceRepository.save(resource);
        
        return mapToResourceResponse(resource);
    }
    
    @Override
    public ProgramResourceResponse updateResource(Integer resourceId, ProgramResourceRequest request, Principal principal) {
        log.info("Updating resource ID: {}", resourceId);
        
        ProgramResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with ID: " + resourceId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, resource.getProgramLesson().getProgramModule().getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to modify this resource");
        }
        
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setFileUrl(request.getFileUrl());
        resource.setFileType(request.getFileType());
        resource.setFileSizeBytes(request.getFileSizeBytes());
        if (request.getOrderIndex() != null) {
            resource.setOrderIndex(request.getOrderIndex());
        }
        
        resource = resourceRepository.save(resource);
        
        return mapToResourceResponse(resource);
    }
    
    @Override
    public void deleteResource(Integer resourceId, Principal principal) {
        log.info("Deleting resource ID: {}", resourceId);
        
        ProgramResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with ID: " + resourceId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, resource.getProgramLesson().getProgramModule().getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to delete this resource");
        }
        
        resourceRepository.delete(resource);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProgramResourceResponse> getLessonResources(Integer lessonId, Principal principal) {
        log.info("Fetching resources for lesson ID: {}", lessonId);
        
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, lesson.getProgramModule().getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to view this lesson's resources");
        }
        
        List<ProgramResourceEntity> resources = resourceRepository.findByLessonIdOrderByOrderIndex(lessonId);
        
        return resources.stream()
                .map(this::mapToResourceResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public void reorderModules(Integer programId, List<Integer> moduleIds, Principal principal) {
        log.info("Reordering modules for program ID: {}", programId);
        
        FitnessProgramEntity program = programRepository.findById(programId)
                .orElseThrow(() -> new IllegalArgumentException("Program not found with ID: " + programId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, program)) {
            throw new UnauthorizedAccessException("You don't have permission to reorder modules");
        }
        
        for (int i = 0; i < moduleIds.size(); i++) {
            final int orderIndex = i;
            final Integer moduleId = moduleIds.get(i);
            ProgramModuleEntity module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + moduleId));
            module.setOrderIndex(orderIndex);
            moduleRepository.save(module);
        }
    }
    
    @Override
    public void reorderLessons(Integer moduleId, List<Integer> lessonIds, Principal principal) {
        log.info("Reordering lessons for module ID: {}", moduleId);
        
        ProgramModuleEntity module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + moduleId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, module.getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to reorder lessons");
        }
        
        for (int i = 0; i < lessonIds.size(); i++) {
            final int orderIndex = i;
            final Integer lessonId = lessonIds.get(i);
            ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
            lesson.setOrderIndex(orderIndex);
            lessonRepository.save(lesson);
        }
    }
    
    @Override
    public void reorderResources(Integer lessonId, List<Integer> resourceIds, Principal principal) {
        log.info("Reordering resources for lesson ID: {}", lessonId);
        
        ProgramLessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
        
        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("User not found"));
        
        if (!isProgramOwner(user, lesson.getProgramModule().getFitnessProgram())) {
            throw new UnauthorizedAccessException("You don't have permission to reorder resources");
        }
        
        for (int i = 0; i < resourceIds.size(); i++) {
            final int orderIndex = i;
            final Integer resourceId = resourceIds.get(i);
            ProgramResourceEntity resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new IllegalArgumentException("Resource not found with ID: " + resourceId));
            resource.setOrderIndex(orderIndex);
            resourceRepository.save(resource);
        }
    }
    
    // Helper methods
    private boolean hasAccessToProgram(UserEntity user, FitnessProgramEntity program) {
        log.info("Checking access for user: {} (role: {}) to program: {} (id: {})", 
                user.getUsername(), user.getRole(), program.getName(), program.getId());
        
        if (user.getRole() == Roles.ADMIN) {
            log.info("User is ADMIN, granting access");
            return true;
        }
        
        if (user.getRole() == Roles.INSTRUCTOR && isProgramOwner(user, program)) {
            log.info("User is INSTRUCTOR and program owner, granting access");
            return true;
        }
        
        if (user.getRole() == Roles.USER) {
            try {
                boolean isEnrolled = userProgramRepository.existsByUserByUserIdAndFitnessProgramByProgramId(user, program);
                log.info("User enrollment check result: {}", isEnrolled);
                return isEnrolled;
            } catch (Exception e) {
                log.error("Error checking user enrollment: {}", e.getMessage(), e);
                // For now, allow access if there's an error checking enrollment
                return true;
            }
        }
        
        log.warn("No access granted for user: {} to program: {}", user.getUsername(), program.getName());
        return false;
    }
    
    private boolean isProgramOwner(UserEntity user, FitnessProgramEntity program) {
        return user.getRole() == Roles.ADMIN || program.getUser().getId().equals(user.getId());
    }
    
    private Integer getNextModuleOrder(Integer programId) {
        Integer maxOrder = moduleRepository.findMaxOrderIndexByProgramId(programId);
        return maxOrder != null ? maxOrder + 1 : 0;
    }
    
    private Integer getNextLessonOrder(Integer moduleId) {
        Integer maxOrder = lessonRepository.findMaxOrderIndexByModuleId(moduleId);
        return maxOrder != null ? maxOrder + 1 : 0;
    }
    
    private Integer getNextResourceOrder(Integer lessonId) {
        Integer maxOrder = resourceRepository.findMaxOrderIndexByLessonId(lessonId);
        return maxOrder != null ? maxOrder + 1 : 0;
    }
    
    private ProgramModuleResponse mapToModuleResponse(ProgramModuleEntity module) {
        List<ProgramLessonResponse> lessons = module.getLessons() != null ?
                module.getLessons().stream()
                        .map(this::mapToLessonResponse)
                        .collect(Collectors.toList()) : List.of();
        
        return ProgramModuleResponse.builder()
                .id(module.getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .orderIndex(module.getOrderIndex())
                .isPublished(module.getIsPublished())
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .lessons(lessons)
                .build();
    }
    
    private ProgramLessonResponse mapToLessonResponse(ProgramLessonEntity lesson) {
        List<ProgramResourceResponse> resources = lesson.getResources() != null ?
                lesson.getResources().stream()
                        .map(this::mapToResourceResponse)
                        .collect(Collectors.toList()) : List.of();
        
        return ProgramLessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .durationMinutes(lesson.getDurationMinutes())
                .orderIndex(lesson.getOrderIndex())
                .isPublished(lesson.getIsPublished())
                .isPreview(lesson.getIsPreview())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .resources(resources)
                .build();
    }
    
    private ProgramResourceResponse mapToResourceResponse(ProgramResourceEntity resource) {
        return ProgramResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .description(resource.getDescription())
                .fileUrl(resource.getFileUrl())
                .fileType(resource.getFileType())
                .fileSizeBytes(resource.getFileSizeBytes())
                .orderIndex(resource.getOrderIndex())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
