package com.example.file.service.impl;

import com.example.common.response.Response;
import com.example.common.utils.RedisKeyUtils;
import com.example.common.utils.elastic.ElasticUserUtils;
import com.example.file.properties.AvatarProperties;
import com.example.file.properties.UploadProperties;
import com.example.file.service.FileService;
import com.example.file.utils.AvatarUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final AvatarProperties avatarProperties;
    private final UploadProperties uploadProperties;

    @Override
    public String upload(MultipartFile file, String type) {
        return null;
    }

    @Override
    public String initAvatar(String random, Long uid) {
//        AvatarUtils.createAvatar(avatarPath, avatarPrefix, avatarRemotePrefix, avatarRemoteSuffix, random, uid);
        return AvatarUtils.createAvatar(uploadProperties.getAvatarPath(), avatarProperties.getPrefix(),
                avatarProperties.getRemotePrefix(), avatarProperties.getRemoteSuffix(), random, uid);
    }

    @Override
    public String uploadAvatar(MultipartFile avatar, String uid) {
        String avatarType = Objects.requireNonNull(avatar.getContentType()).substring(6);
        String avatarName = System.currentTimeMillis() + "." + avatarType;

        File path = new File(uploadProperties.getAvatarPath() + uid);
        if (!path.exists()) {
            boolean ready = path.mkdirs();
            if (!ready) return null;
        }

        File realPath = new File(path.getAbsolutePath() + File.separator + avatarName);

        try {
            avatar.transferTo(realPath);
        } catch (IOException e) {
//            throw new RuntimeException("转移头像文件时出错");
            return null;
        }

        return uploadProperties.getAvatarPath() + uploadProperties.getAvatarMapperPath() + uid + "/" + avatarName;
    }
}
