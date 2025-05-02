package com.example.user;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.example.common.utils.elastic.ElasticUserUtils;
import com.example.common.utils.elastic.ElasticUtils;
import com.example.user.domain.po.User;
import com.example.user.service.UserService;
import com.example.user.utils.UserBeanUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes = UserServiceApplication.class)
public class BBSTest {
    @Resource
    PasswordEncoder passwordEncoder;

    @Test
    public void getPassword() {
        System.out.println(passwordEncoder.encode("qwertyuiopP@789"));
    }

    @Resource
    ElasticsearchClient elasticsearchClient;

    @Resource
    private UserService userService;

    @Test
    public void ESTest() throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "user");
        if (!indexExist) {
            boolean created = ElasticUserUtils.createUserIndex(elasticsearchClient);
            if (created) { log.info("已创建user索引到Elasticsearch"); }
        }

        List<User> users = userService.list();
        List<BulkOperation> bulkOperations = new ArrayList<>();

        users.forEach(user -> {

            user.setPassword(null); //密码不用存储到ES

            bulkOperations.add(BulkOperation.of(bo -> bo.index(io -> io.id(String.valueOf(user.getUid())).document(UserBeanUtils.userEO(user)))));
        });

        BulkResponse bulkResponse = elasticsearchClient.bulk(br -> br.index("user").operations(bulkOperations));

        System.out.println(!bulkResponse.errors()); //如果errors是false则代表执行过程没有错误产生，反之则有错误产生，取反后返回
    }

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void redisTest() {
        System.out.println(redisTemplate.opsForValue().get("123"));
        System.out.println(redisTemplate.opsForValue().get("123") == null);
    }
}
