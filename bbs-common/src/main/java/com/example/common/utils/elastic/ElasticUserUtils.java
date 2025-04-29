package com.example.common.utils.elastic;

import cn.hutool.core.util.ObjectUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.example.common.domain.eo.UserEO;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ElasticUserUtils {
    private static final String INDEX_NAME = "user";

    public static boolean createUserIndex(ElasticsearchClient elasticsearchClient) throws IOException {
        Map<String, Property> map = new HashMap<>();
        map.put("uid", Property.of(p -> p.long_(lnp -> lnp.index(true))));
        map.put("avatar", Property.of(p -> p.keyword(kp -> kp.index(false))));
        map.put("username", Property.of(p -> p.text(tp -> tp.index(true).analyzer("ik_max_word"))));
        map.put("nickname", Property.of(p -> p.text(tp -> tp.index(true).analyzer("ik_max_word"))));
        map.put("email", Property.of(p -> p.keyword(kp -> kp.index(true))));
        map.put("emailVerified", Property.of(p -> p.boolean_(bp -> bp.index(true))));
        map.put("registerIp", Property.of(p -> p.keyword(kp -> kp.index(true))));
        map.put("createTime", Property.of(p -> p.date(dp -> dp.index(true).format("yyyy-MM-dd HH:mm:ss"))));
        map.put("banned", Property.of(p -> p.boolean_(bp -> bp.index(true))));

        CreateIndexResponse indexResponse = elasticsearchClient.indices().create(CreateIndexRequest.of(cir -> cir
                .index(INDEX_NAME)
                .mappings(TypeMapping.of(tm -> tm.properties(map)))
                .settings(is -> is.numberOfShards("1").numberOfReplicas("0"))));

        return indexResponse.acknowledged();
    }

    public static boolean insertAllUserByBulkOperation(ElasticsearchClient elasticsearchClient, List<UserEO> users) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            boolean created = createUserIndex(elasticsearchClient);
            if (created) { log.info("已创建user索引到Elasticsearch"); }
        }

        List<BulkOperation> bulkOperations = new ArrayList<>();

        users.forEach(user -> {
            bulkOperations.add(BulkOperation.of(bo -> bo.index(io -> io.id(String.valueOf(user.getUid())).document(user))));
        });

        BulkResponse bulkResponse = elasticsearchClient.bulk(br -> br.index(INDEX_NAME).operations(bulkOperations));

        return !bulkResponse.errors(); //如果errors是false则代表执行过程没有错误产生，反之则有错误产生，取反后返回
    }

    public static void insertUserToEs(ElasticsearchClient elasticsearchClient, UserEO user) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            createUserIndex(elasticsearchClient);
        }

        elasticsearchClient.index(ir -> ir.index(INDEX_NAME).id(String.valueOf(user.getUid())).document(user));
    }

    public static List<Hit<UserEO>> searchUsersByKey(ElasticsearchClient elasticsearchClient, String key) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            createUserIndex(elasticsearchClient);
        }

        SearchResponse<UserEO> searchResponse = elasticsearchClient.search(sr -> sr
                        .index(INDEX_NAME)
                        .query(q -> q
                                .bool(bq -> bq
                                        .should(obq -> obq
                                                .wildcard(wq -> wq
                                                        .field("username")
                                                        .value("*" + key + "*")))
                                        .should(obq -> obq
                                                .wildcard(wq -> wq
                                                        .field("nickname")
                                                        .value("*" + key + "*")))))
                        .highlight(hl -> hl
                                .requireFieldMatch(false)
                                .fields("username", hlf -> hlf
                                        .preTags("<span style='color: red;'>")
                                        .postTags("</span>"))
                                .fields("nickname", hlf -> hlf
                                        .preTags("<span style='color: red;'>")
                                        .postTags("</span>")))
                ,
                UserEO.class
        );

        if (searchResponse.timedOut() || searchResponse.hits().total().value() == 0) {
            return null;
        }

        List<Hit<UserEO>> userHits = searchResponse.hits().hits();
        userHits.forEach(userHit -> {
            UserEO user = userHit.source();
            String username = "";
            String nickname = "";
            if (ObjectUtil.isNotNull(userHit.highlight())) {
                if (userHit.highlight().containsKey("username")) username = userHit.highlight().get("username").get(0);
                if (userHit.highlight().containsKey("nickname")) nickname = userHit.highlight().get("nickname").get(0);
            }

            if (ObjectUtil.isNotNull(user)) {
                if (ObjectUtil.isNotEmpty(username)) user.setUsername(username);
                if (ObjectUtil.isNotEmpty(nickname)) user.setNickname(nickname);
            }
        });

        return userHits;
    }

    public static void updateUsernameInEs(ElasticsearchClient elasticsearchClient, UserEO user) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            createUserIndex(elasticsearchClient);
        }

        Map<String, String> map = new HashMap<>();
        map.put("username", user.getUsername());

        elasticsearchClient.update(ur -> ur.index(INDEX_NAME).id(String.valueOf(user.getUid())).doc(map), UserEO.class);
    }

    public static void updateUserNicknameInEs(ElasticsearchClient elasticsearchClient, UserEO user) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            createUserIndex(elasticsearchClient);
        }

        Map<String, String> map = new HashMap<>();
        map.put("nickname", user.getNickname());

        elasticsearchClient.update(ur -> ur.index(INDEX_NAME).id(String.valueOf(user.getUid())).doc(map), UserEO.class);
    }

    public static void updateUserAvatarInEs(ElasticsearchClient elasticsearchClient, UserEO user) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            createUserIndex(elasticsearchClient);
        }

        Map<String, String> map = new HashMap<>();
        map.put("avatar", user.getAvatar());

        elasticsearchClient.update(ur -> ur.index(INDEX_NAME).id(String.valueOf(user.getUid())).doc(map), UserEO.class);
    }

    public static void updateUserEmailInEs(ElasticsearchClient elasticsearchClient, UserEO user) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            createUserIndex(elasticsearchClient);
        }

        Map<String, String> map = new HashMap<>();
        map.put("email", user.getEmail());

        elasticsearchClient.update(ur -> ur.index(INDEX_NAME).id(String.valueOf(user.getUid())).doc(map), UserEO.class);
    }

    public static void updateUserEmailVerifiedInEs(ElasticsearchClient elasticsearchClient, UserEO user) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            createUserIndex(elasticsearchClient);
        }

        Map<String, Boolean> map = new HashMap<>();
        map.put("emailVerified", user.getEmailVerified());

        elasticsearchClient.update(ur -> ur.index(INDEX_NAME).id(String.valueOf(user.getUid())).doc(map), UserEO.class);
    }

    public static void updateUserBannedStatusInEs(ElasticsearchClient elasticsearchClient, UserEO user) throws IOException {
        boolean indexExist = ElasticUtils.isIndexExist(elasticsearchClient, INDEX_NAME);
        if (!indexExist) {
            createUserIndex(elasticsearchClient);
        }

        Map<String, Boolean> map = new HashMap<>();
        map.put("banned", user.getBanned());

        elasticsearchClient.update(ur -> ur.index(INDEX_NAME).id(String.valueOf(user.getUid())).doc(map), UserEO.class);
    }
}
