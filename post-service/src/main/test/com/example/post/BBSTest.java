package com.example.post;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.domain.dto.PostDTO;
import com.example.common.utils.elastic.ElasticPostUtils;
import com.example.common.utils.elastic.ElasticUtils;
import com.example.post.domain.po.Post;
import com.example.post.domain.po.PostTag;
import com.example.post.service.PostService;
import com.example.post.utils.PostBeanUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class BBSTest {
    @Resource
    ElasticsearchClient elasticsearchClient;

    @Resource
    private PostService postService;

    @Test
    public void ESTest() throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "post");
        if (!indexExist) {
            ElasticPostUtils.createPostIndex(elasticsearchClient);
        }

        List<Post> posts = postService.list();  // 这里应该分页拿
        List<BulkOperation> bulkOperations = new ArrayList<>();

        posts.forEach(post -> {
            PostDTO postDetail = postService.getPostDetail(post.getId().toString());

            bulkOperations.add(BulkOperation.of(bo -> bo.index(io -> io.id(String.valueOf(postDetail.getId())).document(postDetail))));
        });

        BulkResponse bulkResponse = elasticsearchClient.bulk(br -> br.index("post").operations(bulkOperations));

        System.out.println(!bulkResponse.errors());
    }

}
