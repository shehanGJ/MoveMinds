package com.java.moveminds.services.impl;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.java.moveminds.services.ImageUploadService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    public String uploadImage(MultipartFile file) throws IOException {
        ensureUploadPathExists();
        String fileExtension = getFileExtension(file.getOriginalFilename());
        BufferedImage resizedImage = resizeImage(file);
        return saveResizedImage(file, resizedImage, fileExtension);
    }

    private void ensureUploadPathExists() throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (Files.notExists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    private BufferedImage resizeImage(MultipartFile file) throws IOException {
        BufferedImage srcImage = ImageIO.read(file.getInputStream());
        return Scalr.resize(srcImage, Scalr.Method.QUALITY, 800, 600);
    }

    private String saveResizedImage(MultipartFile file, BufferedImage resizedImage, String fileExtension) throws IOException {
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "Neispravno ime slike.");
        String modifiedFilename = originalFilename.replaceAll("\\s+", "_");
        String fileName = UUID.randomUUID() + "_" + modifiedFilename;
        Path destFile = Paths.get(uploadPath).resolve(fileName);
        ImageIO.write(resizedImage, Objects.requireNonNull(fileExtension), destFile.toFile());
        return fileName;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public void deleteImageFile(String imageUrl) throws IOException {
        Path filePath = Paths.get(uploadPath, imageUrl.substring("/uploads/".length()));
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
