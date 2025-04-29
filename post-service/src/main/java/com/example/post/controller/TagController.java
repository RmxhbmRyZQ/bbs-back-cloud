package com.example.post.controller;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.client.UserClient;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.PostDTO;
import com.example.common.domain.dto.TagDTO;
import com.example.common.domain.dto.TagOptionDTO;
import com.example.common.domain.dto.UserDTO;
import com.example.common.response.Response;
import com.example.common.utils.RedisKeyUtils;
import com.example.post.domain.po.Post;
import com.example.post.domain.po.PostTag;
import com.example.post.domain.po.Tag;
import com.example.post.domain.po.TagOption;
import com.example.post.service.PostService;
import com.example.post.service.TagOptionService;
import com.example.post.service.TagService;
import com.example.post.utils.PostBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final String tagsKey = RedisKeyUtils.getTagsKey();

    private final String tagOptionsKey = RedisKeyUtils.getTagOptionsKey();

    private final TagService tagService;

    private final TagOptionService tagOptionService;

    private final PostService postService;

    private final UserClient userClient;

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/tags")
    public Response<Map<String, List<TagDTO>>> getAllTag() {
        return Response.success("获取标签成功", Map.of("tags", tagService.getAllTag()));
    }

    @GetMapping("/tagsAndOptions")
    public Response<Map<String, Object>> getTagsAndOptions() {
        return Response.success("获取标签和标签选项成功", tagOptionService.getTagsAndOptions());
    }

    @GetMapping("/tag")
    public Response<Map<String, Object>> getTag(@RequestParam("label") String tagLabel, @RequestParam("page") Integer currentPage) {
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("label", tagLabel);
        Tag tag = tagService.getOne(tagQueryWrapper);
        TagDTO tagDTO = PostBeanUtils.tagDTO(tag);

        if (ObjectUtil.isNull(tag)) {
            return null;
        }

        QueryWrapper<TagOption> tagOptionQueryWrapper = new QueryWrapper<>();
        tagOptionQueryWrapper.eq("id", tag.getOptionId());
        TagOption tagOption = tagOptionService.getOne(tagOptionQueryWrapper);
        tagDTO.setTagOption(PostBeanUtils.tagOptionDTO(tagOption));

        int sizePerPage = 30;
        IPage<PostTag> page = new Page<>(currentPage, sizePerPage);

        QueryWrapper<PostTag> postTagQueryWrapper = new QueryWrapper<>();
        postTagQueryWrapper.eq("tag_label", tagLabel);
        IPage<PostTag> postTagIPage = tagService.selectPostTagPage(page, postTagQueryWrapper);
        List<PostTag> postTags = postTagIPage.getRecords();

        List<PostDTO> posts = new ArrayList<>();
        postTags.forEach(postTag -> {
            QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
            postQueryWrapper.eq("id", postTag.getPid());
            Post post = postService.getOne(postQueryWrapper);

            PostDTO postDTO = PostBeanUtils.postDTO(post);
            UserDTO userDTO = userClient.getUserProfileByUid(String.valueOf(postDTO.getUid())).getData();
            postDTO.setAuthor(userDTO.getUsername());
            postDTO.setNickname(userDTO.getNickname());
            postDTO.setAvatar(userDTO.getAvatar());

            posts.add(postDTO);
        });

        Map<String, Object> map = new HashMap<>();
        map.put("tag", tagDTO);
        map.put("posts", posts);
        map.put("totalItems", Integer.parseInt(String.valueOf(page.getTotal())));
        map.put("sizePerPage", Integer.parseInt(String.valueOf(page.getSize())));
        map.put("totalPages", Integer.parseInt(String.valueOf(page.getPages())));
        map.put("currentPage", Integer.parseInt(String.valueOf(page.getCurrent())));

        return Response.success("获取标签成功", map);
    }

    @PostMapping("/sys-ctrl/tag")
    public Response<Map<String, TagDTO>> addTag(@RequestBody TagDTO tagDTO) {
        QueryWrapper<Tag> tagNameQueryWrapper = new QueryWrapper<>();
        tagNameQueryWrapper.eq("name", tagDTO.getName());
        boolean tagNameExist = tagService.exists(tagNameQueryWrapper);
        if (tagNameExist) {
            return Response.failed("标签名已存在");
        }

        QueryWrapper<Tag> tagLabelQueryWrapper = new QueryWrapper<>();
        tagLabelQueryWrapper.eq("label", tagDTO.getLabel());
        boolean tagLabelExist = tagService.exists(tagLabelQueryWrapper);
        if (tagLabelExist) {
            return Response.failed("标签英文标识已存在");
        }
        Tag tag = PostBeanUtils.tag(tagDTO);

        UserDTO userDTO = ((UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserDTO();
        tag.setCreator(userDTO.getUid());

        boolean saved = tagService.save(tag);
        if (saved) {
            QueryWrapper<TagOption> tagOptionQueryWrapper = new QueryWrapper<>();
            tagOptionQueryWrapper.eq("id", tag.getOptionId());
            TagOption tagOption = tagOptionService.getOne(tagOptionQueryWrapper);
            tagDTO.setTagOption(PostBeanUtils.tagOptionDTO(tagOption));

            redisTemplate.delete(tagsKey);
            getAllTag();
            return Response.success("标签创建成功", Map.of("newTag", tagDTO));
        }
        return Response.failed("标签创建失败");
    }

    @PutMapping("/sys-ctrl/tag")
    public Response<List<TagDTO>> updateTag(@RequestBody Tag tag) {
        boolean updated = tagService.update(tag, null);
        if (updated) {
            redisTemplate.delete(tagsKey);
            return Response.success("更新标签成功", tagService.getAllTag());
        } else return Response.failed("更新标签失败");
    }

    @DeleteMapping("/sys-ctrl/tag")
    public Response<List<TagDTO>> deleteTag(@RequestBody Tag tag) {
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("label", tag.getLabel());
        if (!tagService.exists(tagQueryWrapper)) {
            return Response.failed("找不到该标签");
        }

        if (tagService.isTagUsed(tag)) {
            return Response.failed("该标签已存在主题绑定");
        }

        boolean deleted = tagService.remove(tagQueryWrapper);
        if (deleted) {
            redisTemplate.delete(tagsKey);
            return Response.success("删除标签成功", tagService.getAllTag());
        } else return Response.failed("删除失败");
    }

    @DeleteMapping("/sys-ctrl/tagForce")
    public Response<List<TagDTO>> forceDeleteTag(@RequestBody Tag tag) {
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("label", tag.getLabel());
        if (!tagService.exists(tagQueryWrapper)) {
            return Response.failed("找不到该标签");
        }

        /* 检查是否存在标签类型的label为other的类型，没有则创建 */
        QueryWrapper<TagOption> tagOptionQueryWrapper = new QueryWrapper<>();
        tagOptionQueryWrapper.eq("label", "other");
        TagOption tagOption = tagOptionService.getOne(tagOptionQueryWrapper);
        if (ObjectUtil.isNull(tagOption)) {
            tagOption = new TagOption(null, "其它", "other");
            tagOptionService.save(tagOption);
        }

        /* 检查是否存在标签的label为unclassified的标签，没有则创建 */
        QueryWrapper<Tag> unClassifiedTagQueryWrapper = new QueryWrapper<>();
        unClassifiedTagQueryWrapper.eq("label", "unclassified");
        if (!tagService.exists(unClassifiedTagQueryWrapper)) {
            UserDTO userDTO = ((UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserDTO();
            Tag unclassifiedTag = new Tag(tagOption.getId(), "未分类", "unclassified", "", "");
            unclassifiedTag.setCreator(userDTO.getUid());
            tagService.save(unclassifiedTag);
        }

        /* 更换所有已绑定待删除标签的帖子的标签为未分类 */
        UpdateWrapper<PostTag> postTagUpdateWrapper = new UpdateWrapper<>();
        postTagUpdateWrapper.eq("tag_label", tag.getLabel()).set("tag_label", "unclassified");
        tagService.updatePostTag(postTagUpdateWrapper);

        /* 删除当前标签 */
        boolean deleted = tagService.remove(tagQueryWrapper);
        if (deleted) {
            redisTemplate.delete(tagsKey);
            return Response.success("强制删除标签成功", tagService.getAllTag());
        } else return Response.failed("强制删除标签失败");
    }

    @GetMapping("/sys-ctrl/tagOption")
    public Response<List<TagOptionDTO>> getAllTagOptions() {
        return Response.success("获取标签选项成功", tagOptionService.getAllTagOptions());
    }

    @PostMapping("/sys-ctrl/tagOption")
    public Response<Map<String, TagOption>> addTagOption(@RequestBody TagOption tagOption) {  // 这个操作不需要权限吗
        QueryWrapper<TagOption> tagOptionNameQueryWrapper = new QueryWrapper<>();
        tagOptionNameQueryWrapper.eq("name", tagOption.getName());
        boolean tagOptionNameExists = tagOptionService.exists(tagOptionNameQueryWrapper);
        if (tagOptionNameExists) {
            return Response.failed("类型显示名称已存在");
        }

        QueryWrapper<TagOption> tagOptionLabelQueryWrapper = new QueryWrapper<>();
        tagOptionLabelQueryWrapper.eq("label", tagOption.getLabel());
        boolean tagOptionLabelExists = tagOptionService.exists(tagOptionLabelQueryWrapper);
        if (tagOptionLabelExists) {
            return Response.failed("类型英文标识已存在");
        }

        boolean saved = tagOptionService.save(tagOption);
        if (saved) {
            redisTemplate.opsForList().rightPushIfPresent(tagOptionsKey, tagOption);
            return Response.success("添加标签成功", Map.of("newTagOption", tagOption));
        } else return Response.failed("添加标签类型失败");
    }

    @PutMapping("/sys-ctrl/tagOption")
    public Response<Map<String, TagOption>> updateTagOption(@RequestBody TagOption tagOption) {
        boolean updated = tagOptionService.updateById(tagOption);
        if (updated) {
            redisTemplate.delete(tagOptionsKey);
            getAllTagOptions();  // 这里只更新 tagOption 而不更新 tag 是不是不行，因为 tag 内部有 tagOption
            return Response.success("更新类型成功", Map.of("newOption", tagOption));
        } else return Response.failed("更新类型失败");
    }

    @DeleteMapping("/sys-ctrl/tagOption")
    public Response<List<TagOptionDTO>> deleteTagOption(@RequestBody TagOption tagOption) {
        QueryWrapper<TagOption> tagOptionQueryWrapper = new QueryWrapper<>();
        tagOptionQueryWrapper.eq("label", tagOption.getLabel());
        if (!tagOptionService.exists(tagOptionQueryWrapper)) {
            return Response.failed("找不到该标签类型");
        }

        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("option_id", tagOption.getId());
        if (tagService.exists(tagQueryWrapper)) {
            return Response.failed("当前类型已存在标签绑定");
        }

        boolean deleted = tagOptionService.remove(tagOptionQueryWrapper);
        if (deleted) {
            redisTemplate.delete(tagOptionsKey);
            return Response.success("删除标签类型成功", tagOptionService.getAllTagOptions());
        } return Response.failed("删除标签类型失败");
    }

    @DeleteMapping("/sys-ctrl/tagOptionForce")
    public Response<Object> forceDeleteTagOption(@RequestBody TagOption tagOption) {
        QueryWrapper<TagOption> tagOptionQueryWrapper = new QueryWrapper<>();
        tagOptionQueryWrapper.eq("label", tagOption.getLabel());
        if (!tagOptionService.exists(tagOptionQueryWrapper)) {
            return Response.failed("找不到该标签类型");
        }

        /* 检查是否存在标签类型的label为other的类型，没有则创建 */
        QueryWrapper<TagOption> newTagOptionQueryWrapper = new QueryWrapper<>();
        newTagOptionQueryWrapper.eq("label", "other");
        TagOption newTagOption = tagOptionService.getOne(newTagOptionQueryWrapper);
        if (ObjectUtil.isNull(newTagOption)) {
            newTagOption = new TagOption(null, "其它", "other");
            tagOptionService.save(newTagOption);
        }

        /* 检查是否存在标签的label为unclassified的标签，没有则创建 */
        QueryWrapper<Tag> unClassifiedTagQueryWrapper = new QueryWrapper<>();
        unClassifiedTagQueryWrapper.eq("label", "unclassified");
        if (!tagService.exists(unClassifiedTagQueryWrapper)) {
            UserDTO userDTO = ((UserBO) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserDTO();
            Tag unclassifiedTag = new Tag(newTagOption.getId(), "未分类", "unclassified", "", "");
            unclassifiedTag.setCreator(userDTO.getUid());
            tagService.save(unclassifiedTag);
        }

        /* 将待删除的标签类型下的所有标签指向「其它」类型 */
        UpdateWrapper<Tag> tagUpdateWrapper = new UpdateWrapper<>();
        tagUpdateWrapper.eq("option_id", tagOption.getId()).set("option_id", newTagOption.getId());
        tagService.update(null, tagUpdateWrapper);

        boolean deleted = tagOptionService.remove(tagOptionQueryWrapper);
        if (deleted) {
            redisTemplate.delete(tagOptionsKey);
            return Response.success("强制删除标签类型成功", tagOptionService.getTagsAndOptions());
        } else return Response.failed("强制删除标签类型失败");
    }
}
