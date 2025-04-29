package com.example.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class ElasticsearchProperties {
    private String host;
    private int port;
    private String scheme;
}
