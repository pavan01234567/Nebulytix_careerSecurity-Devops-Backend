package com.neb.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

public class FileUtil {

    private static final String BASE_PATH = "uploads/projects/";

    public static String upload(MultipartFile file) {

        if (file == null || file.isEmpty()) return null;

        try {
            File dir = new File(BASE_PATH);
            if (!dir.exists()) dir.mkdirs();

            String name = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, name);

            Files.copy(file.getInputStream(), dest.toPath());

            return BASE_PATH + name;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
    }
}
