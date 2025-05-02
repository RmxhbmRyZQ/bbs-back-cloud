package com.example.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.client.PostClient;
import com.example.api.client.SensitiveClient;
import com.example.api.client.UserClient;
import com.example.api.dto.UpdateComment;
import com.example.comment.domain.po.Comment;
import com.example.comment.service.CommentService;
import com.example.comment.mapper.CommentMapper;
import com.example.common.domain.dto.UserDTO;
import com.example.common.utils.SensitiveWordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    private final CommentMapper commentMapper;

    private final UserClient userClient;

    private final SensitiveClient sensitiveClient;

    private final PostClient postClient;

    @Override
    public List<Comment> getComments(String pid) {
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getPid, pid).orderByDesc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(lambdaQueryWrapper);
        comments.forEach(comment -> {
            UserDTO userDTO = userClient.getUserProfileByUid(comment.getFromUid().toString()).getData();
            comment.setFromAvatar(userDTO.getAvatar());
            comment.setFromUid(userDTO.getUid());
            comment.setFromNickname(userDTO.getNickname());
            comment.setFromUsername(userDTO.getUsername());
        });
        return comments;
    }

    @Override
    public Integer getRepliesNumOfComment(Integer commentId) {
        return commentMapper.getRepliesNumOfComment(commentId);
    }

    @Override
    public List<Comment> getReplies(String pid, Integer parentId) {
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getParentId, parentId).and(wrapper -> wrapper.eq(Comment::getPid, pid)).orderByDesc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(lambdaQueryWrapper);
        comments.forEach(comment -> {
            UserDTO from = userClient.getUserProfileByUid(comment.getFromUid().toString()).getData();
            comment.setFromAvatar(from.getAvatar());
            comment.setFromUid(from.getUid());
            comment.setFromNickname(from.getNickname());
            comment.setFromUsername(from.getUsername());

            UserDTO to = userClient.getUserProfileByUid(comment.getToUid().toString()).getData();
            comment.setToUid(to.getUid());
            comment.setToNickname(to.getNickname());
            comment.setToUsername(to.getUsername());
        });
        return comments;
    }

    @Override
    public Boolean doComment(Comment comment) {
        List<String> sensitiveWords = sensitiveClient.getSensitiveWord().getData();
        String commentContent = SensitiveWordUtils.stringSearchEx2Filter(comment.getContent(), sensitiveWords);
//        comment.setContent(HtmlUtil.escape(commentContent));
        comment.setParentId(0);
        comment.setReplyId(0);

        boolean saved = save(comment);
        if (saved) {
            boolean updated = postClient.updateComment(new UpdateComment(comment.getPid(), comment.getCreateTime())).getData();
            if (updated) {
                comment.setContent(commentContent);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean doReply(Comment comment) {
        List<String> sensitiveWords = sensitiveClient.getSensitiveWord().getData();
        String replyText = SensitiveWordUtils.stringSearchEx2Filter(comment.getContent(), sensitiveWords);
//        comment.setContent(HtmlUtil.escape(replyText));

        boolean saved = save(comment);
        if (saved) {
            UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", comment.getParentId()).setSql("replies_num = replies_num + 1");
            boolean update = update(updateWrapper);
            if (update) {
                comment.setContent(replyText);
                return true;
            }
        }

        return false;
    }
}
