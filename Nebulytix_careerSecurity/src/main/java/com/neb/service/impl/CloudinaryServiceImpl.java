package com.neb.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.neb.service.CloudinaryService;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    // Upload using byte[]
//    @Override
//    public String uploadFile(byte[] fileBytes, String fileName, String folder, String resourceType) {
//        try {
//            Map<String, Object> uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.asMap(
//                    "folder", folder,
//                    "public_id", fileName,
//                    "resource_type", resourceType
//            ));
//            return (String) uploadResult.get("secure_url");
//        } catch (Exception e) {
//            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage(), e);
//        }
//    }

    // Upload using MultipartFile
    @Override
    public String uploadFile(MultipartFile file, String folder, String resourceType) {
        try {
            return uploadFile(file.getBytes(), file.getOriginalFilename(), folder, resourceType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload MultipartFile to Cloudinary", e);
        }
    }

    // Delete file
    @Override
    public void deleteFile(String fileUrl) {
        try {
            String publicId = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.lastIndexOf("."));
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary delete failed: " + e.getMessage(), e);
        }
    }

    // Download file
    @Override
    public byte[] downloadFile(String fileUrl) {
        try (InputStream inputStream = new URL(fileUrl).openStream()) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary download failed: " + e.getMessage(), e);
        }
    }
    
 // Inside your CloudinaryService.java
    public String uploadFile(byte[] bytes, String fileName, String folder, String resourceType) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
            "public_id", fileName,
            "folder", folder,
            "resource_type", resourceType // This will now be "image"
        ));
        return (String) uploadResult.get("secure_url");
    }
}
