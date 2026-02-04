package com.neb.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

   
//    String uploadFile(byte[] fileBytes, String fileName, String folder, String resourceType);
    public String uploadFile(byte[] bytes, String fileName, String folder, String resourceType) throws IOException;

 
    String uploadFile(MultipartFile file, String folder, String resourceType);

    void deleteFile(String fileUrl);

    byte[] downloadFile(String fileUrl);
}
