package com.java.moveminds.services.impl;

import com.java.moveminds.services.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {
    
    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;
    
    @Value("${app.file.base-url:http://localhost:8081/files}")
    private String baseUrl;
    
    @Override
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        log.info("Storing file: {} in directory: {}", file.getOriginalFilename(), subDirectory);
        
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFilename);
        
        // Copy file to target location
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("File stored successfully: {}", filePath);
        return uniqueFilename;
    }
    
    @Override
    public Path getFilePath(String filename, String subDirectory) {
        return Paths.get(uploadDir, subDirectory, filename);
    }
    
    @Override
    public boolean deleteFile(String filename, String subDirectory) {
        try {
            Path filePath = getFilePath(filename, subDirectory);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", filename, e);
            return false;
        }
    }
    
    @Override
    public String getFileUrl(String filename, String subDirectory) {
        return baseUrl + "/" + subDirectory + "/" + filename;
    }
}
