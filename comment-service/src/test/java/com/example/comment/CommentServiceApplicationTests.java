package com.example.comment;

import com.example.comment.domain.po.Comment;
import com.example.comment.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class CommentServiceApplicationTests {
    @Resource
    private CommentService commentService;

    @Test
    void contextLoads() {
    }


    @Test
    public void CommentComplete() {
        List<Comment> list = commentService.list();
        list.forEach(comment -> {
            if (comment.getFromUsername() == null) {
                comment.setRepliesNum(commentService.getRepliesNumOfComment(comment.getId()));
                System.out.println(commentService.updateById(comment));
            }
        });
    }
}
