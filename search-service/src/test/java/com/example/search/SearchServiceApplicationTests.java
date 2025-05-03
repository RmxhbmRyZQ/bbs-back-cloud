package com.example.search;

import cn.hutool.core.bean.BeanUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.api.client.PostClient;
import com.example.api.client.UserClient;
import com.example.common.domain.dto.UserDTO;
import com.example.common.domain.eo.UserEO;
import com.example.common.response.Response;
import com.example.common.utils.elastic.ElasticUserUtils;
import com.example.common.utils.elastic.ElasticUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
class SearchServiceApplicationTests {
    @Resource
    private ElasticsearchClient elasticsearchClient;
    @Resource
    private UserClient userClient;
    @Resource
    private PostClient postClient;

    @Test
    void contextLoads() {
    }

    @Test
    void insertAllPostToEs() {

        try {
            // 获取用户总数
            Long total = userClient.getTotalUserNumber().getData();
            if (total == null || total == 0) {
                log.error("用户总数为 0");
                return;
            }

            int pageSize = 100;
            int totalPages = (int) Math.ceil((double) total / pageSize);

            // 分页查询并批量插入
            for (int page = 1; page <= totalPages; page++) {
                Response<Object> response = userClient.getUserPage(page, pageSize);
                if (response == null || response.getData() == null) {
                    continue;
                }

                Map<String, Object> dataMap = (Map<String, Object>) response.getData();
                List<Map<String, String>> records = (List<Map<String, String>>) dataMap.get("records");

                // 构建 UserEO 列表
                List<UserEO> userEOList = new ArrayList<>();
                for (Map<String, String> record : records) {
                    UserEO userEO = new UserEO();
                    BeanUtil.fillBeanWithMap(record, userEO, true);
                    userEOList.add(userEO);
                }

                // 批量插入
                boolean success = ElasticUserUtils.insertAllUserByBulkOperation(elasticsearchClient, userEOList);
                if (!success) {
                    log.warn("第 {} 页批量插入 Elasticsearch 失败", page);
                }
            }


        } catch (Exception e) {
            log.error("插入 Elasticsearch 出现异常", e);
        }
    }
}
