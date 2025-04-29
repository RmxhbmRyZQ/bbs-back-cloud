package com.example.common.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.example.common.properties.ElasticsearchProperties;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
@ConditionalOnProperty("elasticsearch.host")
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfig {
    @Resource
    private ElasticsearchProperties properties;

    @Bean
    public RestClient restClient() {
        restClient = RestClient.builder(
                new HttpHost(properties.getHost(), properties.getPort(), properties.getScheme())
        ).setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(5000)  // 连接超时
                        .setSocketTimeout(60000)  // 读超时，从30秒拉高到60秒
        ).build();
        return restClient;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        RestClientTransport restClientTransport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(restClientTransport);
    }

    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient(RestClient restClient) {
        RestClientTransport restClientTransport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );
        return new ElasticsearchAsyncClient(restClientTransport);
    }

    private RestClient restClient;

    @PreDestroy
    public void destroy() {
        if (restClient != null) {
            try {
                restClient.close();
            } catch (IOException e) {
                log.error("Failed to close RestClient", e);
            }
        }
    }
}
