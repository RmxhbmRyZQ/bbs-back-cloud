package com.example.bbsinit;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

@SpringBootTest
class BbsInitApplicationTests {

    @Test
    void contextLoads() {
    }

    @Value("${spring.cloud.nacos.server-addr}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.username}")
    private String username;

    @Value("${spring.cloud.nacos.password}")
    private String password;

    private static final String BASE_PATH = "C:\\Users\\RmxhbmRyZQ\\Desktop\\find-work\\Java-project-study\\flip-master\\bbs-back\\";

    @Test
    public void initConfig() throws NacosException, IOException {
        // Nacos 服务器地址
        String namespace = ""; // 可选，使用默认命名空间可留空

        // 配置服务
        Properties properties = new Properties();
        properties.setProperty("serverAddr", serverAddr);
        properties.setProperty("namespace", namespace);
        properties.setProperty("username", username);
        properties.setProperty("password", password);

        ConfigService configService = NacosFactory.createConfigService(properties);

        // 文件路径（你的项目路径下的 config 文件夹）
        String[] files = {
                BASE_PATH + "config/mysql.yaml",
                BASE_PATH + "config/security.yaml",
                BASE_PATH + "config/elasticsearch.yaml",
                BASE_PATH + "config/rabbitmq.yaml",
                BASE_PATH + "config/seata.yaml",
                BASE_PATH + "config/sentinel.yaml",
                BASE_PATH + "config/openfeign.yaml",
                BASE_PATH + "config/redis.yaml",
        };

        for (String filePath : files) {
            File file = new File(filePath);
            String content = Files.readString(file.toPath());

            String dataId = file.getName(); // 一般可用文件名当作 dataId
            String group = "DEFAULT_GROUP"; // 你可以自定义 group

            boolean isSuccess = configService.publishConfig(dataId, group, content, ConfigType.YAML.getType());
            System.out.println("上传 " + dataId + ": " + (isSuccess ? "成功" : "失败"));
        }
    }
}
