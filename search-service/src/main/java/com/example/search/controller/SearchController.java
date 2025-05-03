package com.example.search.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.api.client.PostClient;
import com.example.api.client.UserClient;
import com.example.common.domain.dto.PostDTO;
import com.example.common.domain.dto.UserDTO;
import com.example.common.domain.eo.PostEO;
import com.example.common.domain.eo.UserEO;
import com.example.common.response.Response;
import com.example.common.response.ResponseCode;
import com.example.common.utils.elastic.ElasticPostUtils;
import com.example.common.utils.elastic.ElasticUserUtils;
import com.example.common.utils.elastic.ElasticUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final ElasticsearchClient elasticsearchClient;
    private final UserClient userClient;
    private final PostClient postClient;

    @GetMapping("/post")
    public Response<Map<String, List<PostEO>>> searchPostsByKey(@RequestParam String keyword) {
        try {
            List<Hit<PostEO>> postHits = ElasticPostUtils.searchPostsByKey(elasticsearchClient, keyword);
            if (ObjectUtil.isNull(postHits)) {
                return Response.success("没有与关键词匹配的结果");
            } else {
                List<PostEO> searchedPosts = new ArrayList<>();
                postHits.forEach(postHit -> {
                    searchedPosts.add(postHit.source());
                });
                return Response.success("查询成功", Map.of("posts", searchedPosts));
            }
        } catch (IOException e) {
            return Response.failed(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), "服务器错误，请稍后重试");
        }
    }

    @GetMapping("/user")
    public Response<Map<String, List<UserEO>>> searchUsersByKey(@RequestParam String keyword) {
        try {
            List<Hit<UserEO>> userHits = ElasticUserUtils.searchUsersByKey(elasticsearchClient, keyword);
            if (ObjectUtil.isNull(userHits)) {
                return Response.success("没有与关键词匹配的结果");
            } else {
                List<UserEO> searchedUsers = new ArrayList<>();
                userHits.forEach(userHit -> {
                    searchedUsers.add(userHit.source());
                });
                return Response.success("查询成功", Map.of("users", searchedUsers));
            }
        } catch (IOException e) {
            return Response.failed(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), "服务器错误，请稍后重试");
        }
    }

    @PostMapping("/addPosts")
    public Response<String> insertAllPostToEs() {
        try {

            // 获取帖子总数
            Long total = postClient.getTotalPostNumber().getData();
            if (total == null || total == 0) {
                return Response.failed("没有帖子数据");
            }

            int pageSize = 30; // getPostList 每页固定 30 条
            int totalPages = (int) Math.ceil((double) total / pageSize);

            for (int page = 1; page <= totalPages; page++) {
                Map<String, Object> response = (Map<String, Object>) postClient.getPostPage(page, pageSize).getData();
                if (response == null) {
                    continue;
                }

                List<Map<String, Object>> postMaps = (List<Map<String, Object>>) response.get("records");
                if (postMaps == null || postMaps.isEmpty()) {
                    continue;
                }

                List<PostEO> postEOList = new ArrayList<>();
                for (Map<String, Object> postMap : postMaps) {
                    // 使用 Hutool 将 Map 转为 PostDTO，再转为 PostEO
                    PostEO postEO = new PostEO();
                    BeanUtil.fillBeanWithMap(postMap, postEO, true);

                    postEOList.add(postEO);
                }

                // 批量写入 ES
                boolean success = ElasticPostUtils.insertAllPostByBulkOperation(elasticsearchClient, postEOList);
                if (!success) {
                    log.warn("第 {} 页批量插入 Elasticsearch 失败", page);
                }
            }

            return Response.success("成功将所有帖子数据插入 Elasticsearch");
        } catch (Exception e) {
            log.error("插入 Elasticsearch 过程中出现异常", e);
            return Response.failed("插入失败：" + e.getMessage());
        }

    }

    @PostMapping("/addUsers")
    public Response<String> insertAllUserToEs() {
        try {
            // 获取用户总数
            Long total = userClient.getTotalUserNumber().getData();
            if (total == null || total == 0) {
                return Response.failed("用户总数为 0");
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

            return Response.success("成功将所有用户信息插入 Elasticsearch");

        } catch (Exception e) {
            log.error("插入 Elasticsearch 出现异常", e);
            return Response.failed("插入失败：" + e.getMessage());
        }
    }
}
