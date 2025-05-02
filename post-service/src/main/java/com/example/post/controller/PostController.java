package com.example.post.controller;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.api.client.SensitiveClient;
import com.example.api.client.UserClient;
import com.example.api.dto.UpdateComment;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.PostDTO;
import com.example.common.domain.dto.TagDTO;
import com.example.common.domain.dto.UserDTO;
import com.example.common.response.Response;
import com.example.common.response.ResponseCode;
import com.example.common.utils.SensitiveWordUtils;
import com.example.common.utils.elastic.ElasticPostUtils;
import com.example.post.domain.po.Post;
import com.example.post.domain.po.PostTag;
import com.example.post.domain.po.Tag;
import com.example.post.service.PostService;
import com.example.post.service.TagService;
import com.example.post.utils.PostBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final SensitiveClient sensitiveClient;

    private final UserClient userClient;

    private final ElasticsearchClient elasticsearchClient;

    private final TagService tagService;

    /**
     * 发布帖子
     * @param postDTO 帖子
     * @return result
     */
    @PostMapping("/post")
    public Response<Map<String, String>> doPost(@RequestBody PostDTO postDTO) {
        UserBO userBO = (UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO userDTO = userBO.getUserDTO();
        postDTO.setUid(userDTO.getUid());

        List<String> sensitiveWords = sensitiveClient.getSensitiveWord().getData();
        String postTitle = SensitiveWordUtils.stringSearchEx2Filter(postDTO.getTitle(), sensitiveWords);
        String postContent = SensitiveWordUtils.stringSearchEx2Filter(postDTO.getContent(), sensitiveWords);

        postDTO.setTitle(postTitle);
        postDTO.setContent(postContent);
        Post post = PostBeanUtils.post(postDTO);


        boolean saved = postService.save(post);
        if (saved) {
            postDTO.getTags().forEach(tag -> {
                postService.savePostTag(new PostTag(null, post.getId(), tag.getLabel(), userDTO.getUid(), null));
            });

            try {
                ElasticPostUtils.insertPostToEs(elasticsearchClient, PostBeanUtils.postEO(post));
            } catch (IOException e) {
                e.printStackTrace();
                return Response.success("帖子发布成功", Map.of("postId", String.valueOf(post.getId())));
            }

            return Response.success("帖子发布成功", Map.of("postId", String.valueOf(post.getId())));
        }

        return Response.failed("帖子发布失败");
    }

    @PostMapping("/postComment")
    Response<Boolean> updateComment(@RequestBody UpdateComment updateComment) {
        UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", updateComment.getPid())
                .setSql("reply_number = reply_number + 1")
                .setSql("last_comment_time = '" + updateComment.getCreateTime() + "'");
        boolean update = postService.update(updateWrapper);
        return Response.success("操作成功", update);
    }

    @PutMapping("/postContent")
    public Response<Map<String, String>> doEditContent(@RequestBody PostDTO post) {
        // 为什么上面插入不进行判断
        if (post.getContent().length() >= 65536) {
            return Response.failed("内容长度超限");
        }

        List<String> sensitiveWords = sensitiveClient.getSensitiveWord().getData();
        String postContent = SensitiveWordUtils.stringSearchEx2Filter(post.getContent(), sensitiveWords);

        UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", post.getId()).set("content", postContent);
        boolean updated = postService.update(updateWrapper);
        if (updated) {
            try {
                // 为什么上面插入不进行判断，去除html里面的所有tag，因为内部clean了
                post.setContent(HtmlUtil.cleanHtmlTag(postContent));
                ElasticPostUtils.updatePostContentInEs(elasticsearchClient, PostBeanUtils.postEO(post));
            } catch (IOException e) {
                return Response.success("修改内容成功", Map.of("content", postContent));
            }
            return Response.success("修改内容成功", Map.of("content", postContent));
        }

        return Response.failed("修改内容失败");
    }

    @PutMapping("/postTitle")
    public Response<Map<String, String>> doEditTitle(@RequestParam String pid, @RequestParam String title) {
        if (title.length() > 100) {
            return Response.failed("标题长度超限");
        }

        List<String> sensitiveWords = sensitiveClient.getSensitiveWord().getData();
        String postTitle = SensitiveWordUtils.stringSearchEx2Filter(title, sensitiveWords);

        UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", pid).set("title", HtmlUtil.escape(postTitle));  // 将html都换成转义字符，上面的内容更新也换成这样更好
        boolean updated = postService.update(null, updateWrapper);
        if (updated) {
            try {
                Post post = new Post();
                post.setId(Long.parseLong(pid));
                post.setTitle(postTitle);
                ElasticPostUtils.updatePostTitleInEs(elasticsearchClient, PostBeanUtils.postEO(post));
            } catch (IOException e) {
                return Response.success("修改标题成功", Map.of("title", postTitle));
            }
            return Response.success("修改标题成功", Map.of("title", postTitle));
        }

        return Response.failed("修改标题失败");
    }

    /**
     * 获取各种帖子列表
     * @param node 帖子列表类型
     * @param page 帖子所处列表的当前页码
     * @return result
     */
    @GetMapping("/list")
    public Response<Map<String, Object>> getPostList(@RequestParam String node, @RequestParam(required = false) Integer page) {
        // 这里应该是可以加redis的，但是暂时实力不够，以后再加
        if ("latestPost".equalsIgnoreCase(node))
            return Response.success("获取最新帖子成功", postService.getLatestPostList(page));

        if ("allPost".equalsIgnoreCase(node))
            return Response.success("获取所有帖子成功", postService.getALlPostList(page));

        if ("hotPost".equalsIgnoreCase(node))
            return Response.success("获取热门帖子成功", postService.getHotPostList(page));

        return Response.failed("节点错误");
    }

    @GetMapping("/userPosts")
    public Response<Object> getUserPosts(@RequestParam String username) {
        UserDTO userDTO = userClient.getUserProfile(username).getData();
        if (ObjectUtil.isNotNull(userDTO)) {
            QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
            postQueryWrapper.eq("uid", userDTO.getUid())
                    .select("id, uid, title, create_time, priority, status, type, reply_number, view_number")
                    .orderByDesc("create_time");

            List<Post> posts = postService.list(postQueryWrapper);

            if (ObjectUtil.isNotNull(posts)) {
                return Response.success("获取用户已发布帖子成功", Map.of("posts", posts));
            }

            return Response.failed("获取用户已发布帖子失败");
        }

        return Response.failed("无此用户，查询帖子失败");
    }

    /**
     * 获取帖子详情
     * @param pid 帖子ID
     * @return result
     */
    @GetMapping("/postInfo")
    public Response<Map<String, Object>> getPostInfo(@RequestParam String pid) {

        UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", pid).setSql("view_number = view_number + 1");
        boolean update = postService.update(updateWrapper);

        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.eq("id", pid);
        Post post = postService.getOne(postQueryWrapper);

        if (ObjectUtil.isNull(post)) {
            return Response.failed(ResponseCode.NOT_FOUND.getCode(), "请检查URL是否输入正确");
        }
        PostDTO postDTO = PostBeanUtils.postDTO(post);

        List<PostTag> postTags = postService.getPostTags(pid);
        List<TagDTO> tags = new ArrayList<>();
        postTags.forEach(tagPost -> {
            QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
            tagQueryWrapper.eq("label", tagPost.getTagLabel());
            Tag tag = tagService.getOne(tagQueryWrapper);
            tags.add(PostBeanUtils.tagDTO(tag));
        });
        postDTO.setTags(tags);

        UserDTO userDTO = userClient.getUserProfileByUid(String.valueOf(postDTO.getUid())).getData();

        Map<String, Object> map = new HashMap<>();
        map.put("post", postDTO);
        map.put("id", userDTO.getId());
        map.put("uid", post.getUid());
        map.put("username", userDTO.getUsername());
        map.put("nickname", userDTO.getNickname());
        map.put("avatar", userDTO.getAvatar());
        map.put("role", userDTO.getRole());

        return Response.success("获取帖子详情成功", map);
    }

    @GetMapping("/poseCount")
    Response<Long> getTotalPostNumber() {
        return Response.success("获取文章总数成功", postService.count());
    }

    @PutMapping("/tagOfPost")
    public Response<Map<String, List<TagDTO>>> changeTagOfPost(@RequestBody PostDTO post) {
        UserBO userBO = (UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long pid = post.getId();

        //更新前，把原有的标签记录删掉
        postService.deletePostTags(pid);

        List<TagDTO> newTags = new ArrayList<>();
        post.getTags().forEach(tag -> {
            postService.addTagToPost(new PostTag(null, pid, tag.getLabel(), userBO.getUserDTO().getUid(), null));

            QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
            tagQueryWrapper.eq("label", tag.getLabel()).select("label, name");
            newTags.add(PostBeanUtils.tagDTO(tagService.getOne(tagQueryWrapper)));
        });

        post.setTags(newTags);
        return Response.success("已更新标签", Map.of("newTags", newTags));
    }
}
