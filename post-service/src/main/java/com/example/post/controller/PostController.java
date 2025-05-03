package com.example.post.controller;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.example.post.utils.PostMessageQueue;
import com.example.post.utils.PostRedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final SensitiveClient sensitiveClient;

    private final UserClient userClient;

    private final ElasticsearchClient elasticsearchClient;

    private final TagService tagService;

    private final PostRedisUtils postRedisUtils;

    private final PostMessageQueue postMessageQueue;

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

            postMessageQueue.sendInsert(PostBeanUtils.postEO(post));
//            try {
//                ElasticPostUtils.insertPostToEs(elasticsearchClient, PostBeanUtils.postEO(post));
//            } catch (IOException e) {
//                e.printStackTrace();
//                return Response.success("帖子发布成功", Map.of("postId", String.valueOf(post.getId())));
//            }

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

        if (update) {
            postRedisUtils.updateReplyAndTime(updateComment.getPid().toString(), updateComment.getCreateTime());
        }

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
            postRedisUtils.deletePostDetail(post.getId().toString());
            post.setContent(HtmlUtil.cleanHtmlTag(postContent));
            postMessageQueue.sendUpdateContent(PostBeanUtils.postEO(post));
//            try {
//                // 为什么上面插入不进行判断，去除html里面的所有tag，因为内部clean了
//                ElasticPostUtils.updatePostContentInEs(elasticsearchClient, PostBeanUtils.postEO(post));
//            } catch (IOException e) {
//                return Response.success("修改内容成功", Map.of("content", postContent));
//            }
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
        boolean updated = postService.update(updateWrapper);
        if (updated) {
            postRedisUtils.deletePostDetail(pid);
            Post post = new Post();
            post.setId(Long.parseLong(pid));
            post.setTitle(postTitle);
            postMessageQueue.sendUpdateTitle(PostBeanUtils.postEO(post));
//            try {
//                ElasticPostUtils.updatePostTitleInEs(elasticsearchClient, PostBeanUtils.postEO(post));
//            } catch (IOException e) {
//                return Response.success("修改标题成功", Map.of("title", postTitle));
//            }
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

    @GetMapping("/post/page")
    public Response<Object> getPostPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // 使用 MyBatis-Plus 分页查询
        Page<Post> userPage = new Page<>(page, size);
        Page<Post> resultPage = postService.page(userPage);

        // 转换为 UserDTO 列表
        List<PostDTO> dtoList = resultPage.getRecords().stream()
                .map(PostBeanUtils::postDTO)
                .collect(Collectors.toList());

        // 构建返回结构
        Map<String, Object> map = new HashMap<>();
        map.put("total", resultPage.getTotal());
        map.put("pages", resultPage.getPages());
        map.put("current", resultPage.getCurrent());
        map.put("size", resultPage.getSize());
        map.put("records", dtoList);

        return Response.success("分页获取帖子信息成功", map);
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

//        UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("id", pid).setSql("view_number = view_number + 1");
//        boolean update = postService.update(updateWrapper);
        delayPostUpdate(pid);  // 每次访问都更新数据库不太利于高并发，这里使用延迟任务

        PostDTO postDTO = postRedisUtils.getPostDetailFromCache(pid);
        if (postDTO == null) {

            QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
            postQueryWrapper.eq("id", pid);
            Post post = postService.getOne(postQueryWrapper);

            if (ObjectUtil.isNull(post)) {
                return Response.failed(ResponseCode.NOT_FOUND.getCode(), "请检查URL是否输入正确");
            }
            postDTO = PostBeanUtils.postDTO(post);

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
            postDTO.setAvatar(userDTO.getAvatar());
            postDTO.setAuthor(userDTO.getUsername());
            postDTO.setNickname(userDTO.getNickname());
            postDTO.setRole(userDTO.getRole());
            postDTO.setUuid(userDTO.getId());

            postRedisUtils.cachePostDetail(postDTO);
        } else {
            postDTO.setViewNumber(postRedisUtils.incrViewNumber(postDTO.getId().toString()).intValue());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("post", postDTO);
        map.put("id", postDTO.getUuid());
        map.put("uid", postDTO.getUid());
        map.put("username", postDTO.getAuthor());
        map.put("nickname", postDTO.getNickname());
        map.put("avatar", postDTO.getAvatar());
        map.put("role", postDTO.getRole());

        return Response.success("获取帖子详情成功", map);
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void delayPostUpdate(String pid) {
        if (!postRedisUtils.addPostDelayKey(pid, 1)) return;  // 先设置 delay key

        scheduler.schedule(() -> {  // 设置延迟任务
            PostDTO postDTO = postRedisUtils.getPostDetailFromCache(pid);
            if (postDTO != null) {
                UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", pid).set("view_number", postDTO.getViewNumber());
                postService.update(updateWrapper);
            }
            postRedisUtils.removePostDelayKey(pid);
        }, 1, TimeUnit.MINUTES);
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
