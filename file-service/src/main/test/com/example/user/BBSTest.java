package com.example.user;

import cn.hutool.core.io.FileUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class BBSTest {
    @Resource
    PasswordEncoder passwordEncoder;

    @Test
    public void getPassword() {
        System.out.println(passwordEncoder.encode("qwertyuiopP@789"));
    }

    @Resource
    ElasticsearchClient elasticsearchClient;

    @Test
    public void ESTest() throws IOException {
        File file = FileUtil.file("upload/avatar/test.png");
        System.out.println(file.getAbsolutePath());

    }
}
