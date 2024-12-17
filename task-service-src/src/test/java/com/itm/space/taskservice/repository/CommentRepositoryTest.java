package com.itm.space.taskservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.entity.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.itm.space.taskservice.constant.JsonPathConstant.TASK_SERVICE_ENTITY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommentRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("Тест на сохранение сущности")
    @DataSet(value = "datasets/commentRepositoryTest/tasks.yml", cleanAfter = true, cleanBefore = true)
    void saveComment() {
        Comment comment = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "CommentEntity.json", Comment.class);

        Comment savedComment = commentRepository.save(comment);
        UUID commentId = savedComment.getId();

        assertThat(commentId).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение сущности")
    @DataSet(value = "datasets/commentRepositoryTest/tasks.yml", cleanAfter = true, cleanBefore = true)
    void findComment() {
        Comment comment = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "CommentEntity.json", Comment.class);

        Comment savedComment = commentRepository.save(comment);
        UUID commentId = savedComment.getId();

        Comment findComment = commentRepository.findCommentById(commentId).get();
        assertThat(findComment).isEqualTo(savedComment);
    }
}
