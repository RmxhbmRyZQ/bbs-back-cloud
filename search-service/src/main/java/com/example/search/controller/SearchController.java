package com.example.search.controller;

import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.common.domain.dto.PostDTO;
import com.example.common.domain.eo.UserEO;
import com.example.common.response.Response;
import com.example.common.response.ResponseCode;
import com.example.common.utils.elastic.ElasticPostUtils;
import com.example.common.utils.elastic.ElasticUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final ElasticsearchClient elasticsearchClient;

    @GetMapping("/post")
    public Response<Map<String, List<PostDTO>>> searchPostsByKey(@RequestParam String keyword) {
        try {
            List<Hit<PostDTO>> postHits = ElasticPostUtils.searchPostsByKey(elasticsearchClient, keyword);
            if (ObjectUtil.isNull(postHits)) {
                return Response.success("没有与关键词匹配的结果");
            } else {
                List<PostDTO> searchedPosts = new ArrayList<>();
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
//        boolean success;
//        try {
//            success = ElasticPostUtils.insertAllPostByBulkOperation(elasticsearchClient, postService);
//        } catch (IOException e) {
//            return Response.failed("插入数据失败");
//        }
//        if (success) {
//            return Response.success("插入数据成功");
//        } else return Response.failed("插入数据失败");
        return null;
    }

    @PostMapping("/addUsers")
    public Response<String> insertAllUserToEs() {
//        boolean success;
//        try {
//            success = ElasticUserUtils.insertAllUserByBulkOperation(elasticsearchClient, userService);
//        } catch (IOException e) {
//            return Response.failed("插入数据失败");
//        }
//        if (success) {
//            return Response.success("插入数据成功");
//        } else return Response.failed("插入数据失败");
        return null;
    }
}
