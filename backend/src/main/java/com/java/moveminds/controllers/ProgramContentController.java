package com.java.moveminds.controllers;

import com.java.moveminds.dto.request.ProgramLessonRequest;
import com.java.moveminds.dto.request.ProgramModuleRequest;
import com.java.moveminds.dto.request.ProgramResourceRequest;
import com.java.moveminds.dto.response.ProgramLearningContentResponse;
import com.java.moveminds.dto.response.ProgramLessonResponse;
import com.java.moveminds.dto.response.ProgramModuleResponse;
import com.java.moveminds.dto.response.ProgramResourceResponse;
import com.java.moveminds.services.ProgramContentManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProgramContentController {
    
    private final ProgramContentManagementService programContentService;
    
    // Program Learning Content
    @GetMapping("/{programId}/learning-content")
    public ResponseEntity<ProgramLearningContentResponse> getProgramLearningContent(
            @PathVariable Integer programId,
            Principal principal) {
        log.info("GET /api/programs/{}/learning-content", programId);
        
        ProgramLearningContentResponse response = programContentService.getProgramLearningContent(programId, principal);
        return ResponseEntity.ok(response);
    }
    
    // Module Management
    @PostMapping("/{programId}/modules")
    public ResponseEntity<ProgramModuleResponse> createModule(
            @PathVariable Integer programId,
            @Valid @RequestBody ProgramModuleRequest request,
            Principal principal) {
        log.info("POST /api/programs/{}/modules", programId);
        
        ProgramModuleResponse response = programContentService.createModule(programId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{programId}/modules")
    public ResponseEntity<List<ProgramModuleResponse>> getProgramModules(
            @PathVariable Integer programId,
            Principal principal) {
        log.info("GET /api/programs/{}/modules", programId);
        
        List<ProgramModuleResponse> response = programContentService.getProgramModules(programId, principal);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/modules/{moduleId}")
    public ResponseEntity<ProgramModuleResponse> updateModule(
            @PathVariable Integer moduleId,
            @Valid @RequestBody ProgramModuleRequest request,
            Principal principal) {
        log.info("PUT /api/programs/modules/{}", moduleId);
        
        ProgramModuleResponse response = programContentService.updateModule(moduleId, request, principal);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<Void> deleteModule(
            @PathVariable Integer moduleId,
            Principal principal) {
        log.info("DELETE /api/programs/modules/{}", moduleId);
        
        programContentService.deleteModule(moduleId, principal);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{programId}/modules/reorder")
    public ResponseEntity<Void> reorderModules(
            @PathVariable Integer programId,
            @RequestBody List<Integer> moduleIds,
            Principal principal) {
        log.info("PUT /api/programs/{}/modules/reorder", programId);
        
        programContentService.reorderModules(programId, moduleIds, principal);
        return ResponseEntity.ok().build();
    }
    
    // Lesson Management
    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<ProgramLessonResponse> createLesson(
            @PathVariable Integer moduleId,
            @Valid @RequestBody ProgramLessonRequest request,
            Principal principal) {
        log.info("POST /api/programs/modules/{}/lessons", moduleId);
        
        ProgramLessonResponse response = programContentService.createLesson(moduleId, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<List<ProgramLessonResponse>> getModuleLessons(
            @PathVariable Integer moduleId,
            Principal principal) {
        log.info("GET /api/programs/modules/{}/lessons", moduleId);
        
        List<ProgramLessonResponse> response = programContentService.getModuleLessons(moduleId, principal);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<ProgramLessonResponse> updateLesson(
            @PathVariable Integer lessonId,
            @Valid @RequestBody ProgramLessonRequest request,
            Principal principal) {
        log.info("PUT /api/programs/lessons/{}", lessonId);
        
        ProgramLessonResponse response = programContentService.updateLesson(lessonId, request, principal);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Integer lessonId,
            Principal principal) {
        log.info("DELETE /api/programs/lessons/{}", lessonId);
        
        programContentService.deleteLesson(lessonId, principal);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/modules/{moduleId}/lessons/reorder")
    public ResponseEntity<Void> reorderLessons(
            @PathVariable Integer moduleId,
            @RequestBody List<Integer> lessonIds,
            Principal principal) {
        log.info("PUT /api/programs/modules/{}/lessons/reorder", moduleId);
        
        programContentService.reorderLessons(moduleId, lessonIds, principal);
        return ResponseEntity.ok().build();
    }
    
    // Resource Management
    @PostMapping("/lessons/{lessonId}/resources")
    public ResponseEntity<ProgramResourceResponse> createResource(
            @PathVariable Integer lessonId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "fileUrl", required = false) String fileUrl,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "fileSizeBytes", required = false) Long fileSizeBytes,
            Principal principal) {
        log.info("POST /api/programs/lessons/{}/resources", lessonId);
        
        ProgramResourceRequest request = new ProgramResourceRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setFileUrl(fileUrl);
        request.setFileType(fileType);
        request.setFileSizeBytes(fileSizeBytes);
        
        ProgramResourceResponse response = programContentService.createResource(lessonId, request, file, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/lessons/{lessonId}/resources")
    public ResponseEntity<List<ProgramResourceResponse>> getLessonResources(
            @PathVariable Integer lessonId,
            Principal principal) {
        log.info("GET /api/programs/lessons/{}/resources", lessonId);
        
        List<ProgramResourceResponse> response = programContentService.getLessonResources(lessonId, principal);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/resources/{resourceId}")
    public ResponseEntity<ProgramResourceResponse> updateResource(
            @PathVariable Integer resourceId,
            @Valid @RequestBody ProgramResourceRequest request,
            Principal principal) {
        log.info("PUT /api/programs/resources/{}", resourceId);
        
        ProgramResourceResponse response = programContentService.updateResource(resourceId, request, principal);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/resources/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Integer resourceId,
            Principal principal) {
        log.info("DELETE /api/programs/resources/{}", resourceId);
        
        programContentService.deleteResource(resourceId, principal);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/lessons/{lessonId}/resources/reorder")
    public ResponseEntity<Void> reorderResources(
            @PathVariable Integer lessonId,
            @RequestBody List<Integer> resourceIds,
            Principal principal) {
        log.info("PUT /api/programs/lessons/{}/resources/reorder", lessonId);
        
        programContentService.reorderResources(lessonId, resourceIds, principal);
        return ResponseEntity.ok().build();
    }
}
