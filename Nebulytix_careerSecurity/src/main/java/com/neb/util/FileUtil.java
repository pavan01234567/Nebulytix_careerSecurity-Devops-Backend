package com.neb.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    /**
     * Upload file and return absolute path for DB
     */
    public static String upload(MultipartFile file, String uploadDir) {
        if (file == null || file.isEmpty()) return null;

        try {
            // Ensure upload directory exists
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            // Sanitize filename and add timestamp
            String fileName = System.currentTimeMillis() + "_" +
                    file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            // Full path where file will be stored
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath);

            // ✅ Return absolute path to store in DB
            return filePath.toString();

        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + file.getOriginalFilename(), e);
        }
    }
}
