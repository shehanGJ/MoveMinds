package com.java.moveminds.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface ImageUploadService {
    String uploadImage(MultipartFile file) throws IOException;
    void deleteImageFile(String imageUrl) throws IOException;
}
