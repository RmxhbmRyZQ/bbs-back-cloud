package com.example.file.controller;

import com.example.common.response.Response;
import com.example.common.utils.RedisKeyUtils;
import com.example.common.utils.elastic.ElasticUserUtils;
import com.example.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/init/avatar")
    public Response<String> initAvatar(@RequestParam("random") String random, @RequestParam("uid") Long uid) {
        String url = fileService.initAvatar(random, uid);
        if (url != null) {
            return Response.success("下载成功", url);
        }
        return Response.failed("下载失败", url);
    }

    @PostMapping("/avatar")
    public Response<String> uploadAvatar(@RequestPart("file") MultipartFile avatar, @RequestParam("uid") String uid) {
        String url = fileService.uploadAvatar(avatar, uid);
        if (url != null) {
            return Response.success("上传成功", url);
        }
        return Response.failed("上传失败", url);
    }
}
