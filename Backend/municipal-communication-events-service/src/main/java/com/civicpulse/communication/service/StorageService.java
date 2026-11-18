package com.civicpulse.communication.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * Uploads a file and returns its public URL.
     */
    String uploadFile(MultipartFile file);

    /**
     * Deletes a file by its URL or public identifier.
     */
    void deleteFile(String fileUrl);
}

