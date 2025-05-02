package com.example.post.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.client.UserClient;
import com.example.common.domain.dto.PostDTO;
import com.example.common.domain.dto.UserDTO;
import com.example.post.domain.po.Post;
import com.example.post.domain.po.PostTag;
import com.example.post.mapper.PostTagMapper;
import com.example.post.service.PostService;
import com.example.post.mapper.PostMapper;
import com.example.post.service.TagService;
import com.example.post.utils.PostBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    private final PostMapper postMapper;

    private final PostTagMapper postTagMapper;

    private final UserClient userClient;

    private final TagService tagService;

    @Override
    public void savePostTag(PostTag postTag) {
        postTagMapper.insert(postTag);
    }

    @Override
    public Map<String, Object> getLatestPostList(Integer currentPage) {
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.orderByDesc("create_time");

        return getPostList(currentPage, postQueryWrapper);
    }



    private Map<String, Object> getPostList(Integer currentPage, QueryWrapper<Post> postQueryWrapper) {
        postQueryWrapper.select("id, uid, title, create_time, reply_number, view_number, last_comment_time");
        int current = ObjectUtil.isNull(currentPage) ? 1 : currentPage;
        long sizePerPage = 30L;

        IPage<Post> page = new Page<>(current, sizePerPage);
        IPage<Post> mapsPage = postMapper.selectPage(page, postQueryWrapper);
        List<Post> maps = mapsPage.getRecords();

        List<PostDTO> posts = new ArrayList<>();

        for (Post post : maps) {
            PostDTO postDTO = PostBeanUtils.postDTO(post);
            UserDTO userDTO = userClient.getUserProfileByUid(String.valueOf(postDTO.getUid())).getData();
            postDTO.setAuthor(userDTO.getUsername());
            postDTO.setNickname(userDTO.getNickname());
            postDTO.setAvatar(userDTO.getAvatar());

            posts.add(postDTO);
        }

        Map<String, Object> mapTransfer = new HashMap<>();
        mapTransfer.put("posts", posts);
        mapTransfer.put("totalPage", mapsPage.getPages()); /*总页码数*/
        mapTransfer.put("currentPage", mapsPage.getCurrent()); /*当前页码*/
        mapTransfer.put("sizePerPage", sizePerPage); /*每页显示多少条帖子*/
        mapTransfer.put("totalItems", mapsPage.getTotal()); /*总帖子数*/

        return mapTransfer;
    }

    @Override
    public Map<String, Object> getALlPostList(Integer page) {
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.orderByDesc("last_comment_time");

        return getPostList(page, postQueryWrapper);
    }

    @Override
    public Map<String, Object> getHotPostList(Integer page) {
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.gt("reply_number", 0).orderByDesc("reply_number").orderByDesc("last_comment_time");

        return getPostList(page, postQueryWrapper);
    }

    @Override
    public void deletePostTags(Long pid) {
        QueryWrapper<PostTag> tagPostQueryWrapper = new QueryWrapper<>();
        tagPostQueryWrapper.eq("pid", pid);
        postTagMapper.delete(tagPostQueryWrapper);
    }

    @Override
    public void addTagToPost(PostTag postTag) {
        postTagMapper.insert(postTag);
    }

    @Override
    public List<PostTag> getPostTags(String pid) {
        QueryWrapper<PostTag> tagPostQueryWrapper = new QueryWrapper<>();
        tagPostQueryWrapper.eq("pid", pid);
        return  postTagMapper.selectList(tagPostQueryWrapper);
    }
}




