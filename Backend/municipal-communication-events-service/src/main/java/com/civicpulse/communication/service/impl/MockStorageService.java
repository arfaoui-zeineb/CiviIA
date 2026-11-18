package com.civicpulse.communication.service.impl;

import com.civicpulse.communication.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@ConditionalOnMissingBean(CloudinaryStorageService.class)
@Slf4j
public class MockStorageService implements StorageService {

    @Override
    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String uniqueName = UUID.randomUUID() + "-" + fileName;
        String simulatedUrl = "https://mock-storage.civicpulse.local/uploads/" + uniqueName;
        log.info("MockStorageService: stored '{}' -> '{}'", fileName, simulatedUrl);
        return simulatedUrl;
    }

    @Override
    public void deleteFile(String fileUrl) {
        log.info("MockStorageService: deleted file at '{}'", fileUrl);
    }
}

