package com.example.post.service;

import com.example.common.domain.dto.PostDTO;
import com.example.post.domain.po.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.post.domain.po.PostTag;

import java.util.List;
import java.util.Map;

public interface PostService extends IService<Post> {

    void savePostTag(PostTag postTag);

    Map<String, Object> getLatestPostList(Integer currentPage);

    Map<String, Object> getALlPostList(Integer page);

    Map<String, Object> getHotPostList(Integer page);

    void deletePostTags(Long pid);

    void addTagToPost(PostTag postTag);

    PostDTO getPostDetail(String pid);

    List<PostTag> getPostTags(String pid);
}
