package com.example.common.utils.elastic;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.example.common.domain.eo.PostEO;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ElasticPostUtils {

    public static boolean createPostIndex(ElasticsearchClient elasticsearchClient) throws IOException {
        Map<String, Property> map = new HashMap<>();
        map.put("id", Property.of(p -> p.long_(LongNumberProperty.of(lnp -> lnp.index(true)))));
        map.put("uid", Property.of(p -> p.long_(LongNumberProperty.of(lnp -> lnp.index(true)))));
        map.put("title", Property.of(p -> p.text(TextProperty.of(tp -> tp.analyzer("ik_max_word").index(true)))));
        map.put("content", Property.of(p -> p.text(TextProperty.of(tp -> tp.analyzer("ik_max_word").index(true)))));
        map.put("priority", Property.of(p -> p.float_(FloatNumberProperty.of(fnp -> fnp.index(true)))));
        map.put("type", Property.of(p -> p.integer(IntegerNumberProperty.of(inp -> inp.index(true)))));
        map.put("status", Property.of(p -> p.integer(IntegerNumberProperty.of(inp -> inp.index(true)))));
        map.put("createTime", Property.of(p -> p.date(DateProperty.of(dp -> dp.index(true).format("yyyy-MM-dd HH:mm:ss")))));
        map.put("replyNumber", Property.of(p -> p.integer(IntegerNumberProperty.of(inp -> inp.index(true)))));
        map.put("viewNumber", Property.of(p -> p.integer(IntegerNumberProperty.of(inp -> inp.index(true)))));
        map.put("lastCommentTime", Property.of(p -> p.date(DateProperty.of(dp -> dp.index(true).format("yyyy-MM-dd HH:mm:ss")))));

        CreateIndexResponse indexResponse = elasticsearchClient.indices().create(CreateIndexRequest.of(cir -> cir
                .index("post")
                .mappings(TypeMapping.of(tm -> tm.properties(map)))
                .settings(is -> is.numberOfShards("1").numberOfReplicas("0"))));

        return indexResponse.acknowledged();
    }

    public static boolean insertAllPostByBulkOperation(ElasticsearchClient elasticsearchClient, List<PostEO> posts) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "post");
        if (!indexExist) {
            createPostIndex(elasticsearchClient);
        }

        List<BulkOperation> bulkOperations = new ArrayList<>();

        posts.forEach(post -> {
            post.setContent(StrUtil.cleanBlank(HtmlUtil.cleanHtmlTag(post.getContent())));

            bulkOperations.add(BulkOperation.of(bo -> bo.index(io -> io.id(String.valueOf(post.getId())).document(post))));
        });

        BulkResponse bulkResponse = elasticsearchClient.bulk(br -> br.index("post").operations(bulkOperations));

        return !bulkResponse.errors(); //如果errors是false则代表执行过程没有错误产生，反之则有错误产生，取反后返回
    }

    public static List<Hit<PostEO>> searchPostsByKey(ElasticsearchClient elasticsearchClient, String key) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "post");
        if (!indexExist) {
            createPostIndex(elasticsearchClient);
        }

        SearchResponse<PostEO> searchResponse = elasticsearchClient.search(sr -> sr
                        .index("post")
                        .query(q -> q
                                .multiMatch(mmq -> mmq
                                        .query(key)
                                        .fields("title", "content")
                                        .analyzer("ik_max_word")))
                        .highlight(hl -> hl
                                .requireFieldMatch(false)
                                .fields("title", hlf -> hlf
                                        .preTags("<span style='color: red;'>")
                                        .postTags("</span>"))
                                .fields("content", hlf -> hlf
                                        .preTags("<span style=\"color: red;\">")
                                        .postTags("</span>")))
                ,
                PostEO.class
        );

        if (searchResponse.timedOut() || searchResponse.hits().total().value() == 0) {
            return null;
        }

        List<Hit<PostEO>> postHits = searchResponse.hits().hits();
        postHits.forEach(postHit -> {
            PostEO post = postHit.source();
            String title = "";
            String content = "";
            if (ObjectUtil.isNotNull(postHit.highlight())) {
                if (postHit.highlight().containsKey("title")) title = postHit.highlight().get("title").get(0);
                if (postHit.highlight().containsKey("content")) content = postHit.highlight().get("content").get(0);
            }

            if (ObjectUtil.isNotNull(post)) {
                if (ObjectUtil.isNotEmpty(title)) post.setTitle(title);
                if (ObjectUtil.isNotEmpty(content)) post.setContent(content);
            }
        });

        return postHits;
    }

    public static boolean insertPostToEs(ElasticsearchClient elasticsearchClient, PostEO post) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "post");
        if (!indexExist) {
            createPostIndex(elasticsearchClient);
        }

        post.setContent(StrUtil.cleanBlank(HtmlUtil.cleanHtmlTag(post.getContent())));
        IndexResponse indexResponse = elasticsearchClient.index(ir -> ir.index("post").id(String.valueOf(post.getId())).document(post));
        return "created".equals(indexResponse.result().jsonValue());
    }

    public static boolean updatePostInEs(ElasticsearchClient elasticsearchClient, PostEO post) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "post");
        if (!indexExist) {
            createPostIndex(elasticsearchClient);
        }

        post.setContent(StrUtil.cleanBlank(HtmlUtil.cleanHtmlTag(post.getContent())));
        UpdateResponse<PostEO> updateResponse = elasticsearchClient.update(ur -> ur.index("post").id(String.valueOf(post.getId())).doc(post), PostEO.class);
        return "updated".equals(updateResponse.result().jsonValue());
    }

    public static boolean deletePostInEs(ElasticsearchClient elasticsearchClient, PostEO post) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "post");
        if (!indexExist) {
            createPostIndex(elasticsearchClient);
        }
        DeleteResponse deleteResponse = elasticsearchClient.delete(dr -> dr.index("post").id(String.valueOf(post.getId())));
        return "deleted".equals(deleteResponse.result().jsonValue());
    }

    public static void updatePostTitleInEs(ElasticsearchClient elasticsearchClient, PostEO post) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "post");
        if (!indexExist) {
            createPostIndex(elasticsearchClient);
        }

        Map<String, String> map = new HashMap<>();
        map.put("title", post.getTitle());

        elasticsearchClient.update(ur -> ur.index("post").id(String.valueOf(post.getId())).doc(map), PostEO.class);
    }

    public static void updatePostContentInEs(ElasticsearchClient elasticsearchClient, PostEO post) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, "post");
        if (!indexExist) {
            createPostIndex(elasticsearchClient);
        }

        Map<String, String> map = new HashMap<>();
        map.put("content", StrUtil.cleanBlank(HtmlUtil.cleanHtmlTag(post.getContent())));

        elasticsearchClient.update(ur -> ur.index("post").id(String.valueOf(post.getId())).doc(map), PostEO.class);
    }
}
