package com.example.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.client.UserClient;
import com.example.comment.domain.po.Comment;
import com.example.comment.service.CommentService;
import com.example.comment.mapper.CommentMapper;
import com.example.comment.utils.CommentBeanUtils;
import com.example.common.domain.dto.CommentDTO;
import com.example.common.domain.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    private final CommentMapper commentMapper;

    private final UserClient userClient;

    @Override
    public List<CommentDTO> getComments(String pid) {
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getPid, pid).orderByDesc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(lambdaQueryWrapper);
        List<CommentDTO> commentDTOS = new ArrayList<>();
        comments.forEach(comment -> {
            CommentDTO commentDTO = CommentBeanUtils.commentDTO(comment);
            UserDTO userDTO = userClient.getUserProfileByUid(commentDTO.getFromUid().toString()).getData();
            commentDTO.setFromAvatar(userDTO.getAvatar());
            commentDTO.setFromUid(userDTO.getUid());
            commentDTO.setFromNickname(userDTO.getNickname());
            commentDTO.setFromUsername(userDTO.getUsername());
            commentDTOS.add(commentDTO);
        });
        return commentDTOS;
    }

    @Override
    public Integer getRepliesNumOfComment(Integer commentId) {
        return commentMapper.getRepliesNumOfComment(commentId);
    }

    @Override
    public List<CommentDTO> getReplies(String pid, Integer parentId) {
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getParentId, parentId).and(wrapper -> wrapper.eq(Comment::getPid, pid)).orderByDesc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(lambdaQueryWrapper);
        List<CommentDTO> commentDTOS = new ArrayList<>();
        comments.forEach(comment -> {
            CommentDTO commentDTO = CommentBeanUtils.commentDTO(comment);
            UserDTO from = userClient.getUserProfileByUid(commentDTO.getFromUid().toString()).getData();
            commentDTO.setFromAvatar(from.getAvatar());
            commentDTO.setFromUid(from.getUid());
            commentDTO.setFromNickname(from.getNickname());
            commentDTO.setFromUsername(from.getUsername());
            UserDTO to = userClient.getUserProfileByUid(comment.getToUid().toString()).getData();
            commentDTO.setToUid(to.getUid());
            commentDTO.setToNickname(to.getNickname());
            commentDTO.setToUsername(to.getUsername());
            commentDTOS.add(commentDTO);
        });
        return commentDTOS;
    }
}
