package com.java.moveminds.services;

import com.java.moveminds.dto.request.ProgramLessonRequest;
import com.java.moveminds.dto.request.ProgramModuleRequest;
import com.java.moveminds.dto.request.ProgramResourceRequest;
import com.java.moveminds.dto.response.ProgramLearningContentResponse;
import com.java.moveminds.dto.response.ProgramLessonResponse;
import com.java.moveminds.dto.response.ProgramModuleResponse;
import com.java.moveminds.dto.response.ProgramResourceResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface ProgramContentManagementService {
    
    // Program Learning Content
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    ProgramLearningContentResponse getProgramLearningContent(Integer programId, Principal principal);
    
    // Module Management
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    ProgramModuleResponse createModule(Integer programId, ProgramModuleRequest request, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    ProgramModuleResponse updateModule(Integer moduleId, ProgramModuleRequest request, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    void deleteModule(Integer moduleId, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    List<ProgramModuleResponse> getProgramModules(Integer programId, Principal principal);
    
    // Lesson Management
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    ProgramLessonResponse createLesson(Integer moduleId, ProgramLessonRequest request, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    ProgramLessonResponse updateLesson(Integer lessonId, ProgramLessonRequest request, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    void deleteLesson(Integer lessonId, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    List<ProgramLessonResponse> getModuleLessons(Integer moduleId, Principal principal);
    
    // Resource Management
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    ProgramResourceResponse createResource(Integer lessonId, ProgramResourceRequest request, MultipartFile file, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    ProgramResourceResponse updateResource(Integer resourceId, ProgramResourceRequest request, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    void deleteResource(Integer resourceId, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    List<ProgramResourceResponse> getLessonResources(Integer lessonId, Principal principal);
    
    // Bulk Operations
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    void reorderModules(Integer programId, List<Integer> moduleIds, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    void reorderLessons(Integer moduleId, List<Integer> lessonIds, Principal principal);
    
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR') or hasRole('ROLE_ADMIN')")
    void reorderResources(Integer lessonId, List<Integer> resourceIds, Principal principal);
}
