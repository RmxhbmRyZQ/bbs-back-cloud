package com.example.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String upload(MultipartFile file, String avatar);

    String initAvatar(String random, Long uid);

    String uploadAvatar(MultipartFile avatar, String uid);
}
