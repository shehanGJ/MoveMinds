package com.java.moveminds.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    
    /**
     * Store a file and return the file path
     */
    String storeFile(MultipartFile file, String subDirectory) throws IOException;
    
    /**
     * Get the file path for a given filename
     */
    Path getFilePath(String filename, String subDirectory);
    
    /**
     * Delete a file
     */
    boolean deleteFile(String filename, String subDirectory);
    
    /**
     * Get the file URL for serving files
     */
    String getFileUrl(String filename, String subDirectory);
}
