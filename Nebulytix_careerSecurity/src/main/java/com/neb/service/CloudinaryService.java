package com.neb.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

   
    String uploadFile(byte[] fileBytes, String fileName, String folder, String resourceType);

 
    String uploadFile(MultipartFile file, String folder, String resourceType);

    void deleteFile(String fileUrl);

    byte[] downloadFile(String fileUrl);
}
