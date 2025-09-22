package com.java.moveminds.controllers;

import com.java.moveminds.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FileController {
    
    private final FileStorageService fileStorageService;
    
    @GetMapping("/{subDirectory}/{filename}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String subDirectory,
            @PathVariable String filename) {
        try {
            log.info("Serving file: {} from directory: {}", filename, subDirectory);
            
            Path filePath = fileStorageService.getFilePath(filename, subDirectory);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = getContentType(filename);
                String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
                
                // Determine if file should be displayed inline or downloaded
                String disposition = shouldDisplayInline(filename) ? "inline" : "attachment";
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + encodedFilename + "\"")
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS")
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization")
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.EXPIRES, "0")
                        .body(resource);
            } else {
                log.warn("File not found or not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error serving file: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @RequestMapping(value = "/{subDirectory}/{filename}", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions(@PathVariable String subDirectory, @PathVariable String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization")
                .header(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600")
                .build();
    }
    
    private boolean shouldDisplayInline(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf", "txt", "jpg", "jpeg", "png", "gif" -> true;
            default -> false;
        };
    }
    
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "txt" -> "text/plain";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "zip" -> "application/zip";
            case "rar" -> "application/x-rar-compressed";
            default -> "application/octet-stream";
        };
    }
}
