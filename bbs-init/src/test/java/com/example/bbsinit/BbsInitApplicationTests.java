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
    public void initConfig() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", serverAddr);
        properties.setProperty("namespace", "");
        properties.setProperty("username", username);
        properties.setProperty("password", password);

        ConfigService configService = NacosFactory.createConfigService(properties);

        initNacos(configService);
        initSentinel(configService);
    }

    /**
     * 上传 config/nacos 下的配置文件至 DEFAULT_GROUP
     */
    private void initNacos(ConfigService configService) throws IOException, NacosException {
        File configDir = new File(BASE_PATH + "config/nacos");
        File[] files = configDir.listFiles();

        if (files == null) {
            System.out.println("Nacos 配置文件目录不存在或为空");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                String dataId = file.getName();
                String content = Files.readString(file.toPath());
                boolean isSuccess = configService.publishConfig(dataId, "DEFAULT_GROUP", content, ConfigType.YAML.getType());
                System.out.println("上传 NACOS: " + dataId + " -> " + (isSuccess ? "成功" : "失败"));
            }
        }
    }

    /**
     * 上传 config/sentinel 下的配置文件至 SENTINEL_GROUP
     */
    private void initSentinel(ConfigService configService) throws IOException, NacosException {
        File configDir = new File(BASE_PATH + "config/sentinel");
        File[] files = configDir.listFiles();

        if (files == null) {
            System.out.println("Sentinel 配置文件目录不存在或为空");
            return;
        }

        for (File file : files) {
            if (file.isFile()) {
                String dataId = file.getName();
                String content = Files.readString(file.toPath());
                boolean isSuccess = configService.publishConfig(dataId, "SENTINEL_GROUP", content, ConfigType.JSON.getType());
                System.out.println("上传 SENTINEL: " + dataId + " -> " + (isSuccess ? "成功" : "失败"));
            }
        }
    }
}
