package com.itm.space.taskservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.entity.CommentToLikes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static com.itm.space.taskservice.constant.JsonPathConstant.TASK_SERVICE_ENTITY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommentToLikeRepositoryTest extends BaseIntegrationTest {

    @Autowired
    CommentToLikesRepository commentToLikesRepository;

    @Test
    @DisplayName("Статус 200: Тест на сохранение сущности")
    @DataSet(value = "datasets/commentToLikeRepositoryTest/commentLike.yml", cleanAfter = true, cleanBefore = true)
    void saveCommentToLike() {
        CommentToLikes commentToLike = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "CommentToLikesEntity.json", CommentToLikes.class);

        CommentToLikes savedCommentToLike = commentToLikesRepository.save(commentToLike);
        UUID commentToLikeId = savedCommentToLike.getId();

        assertThat(commentToLikeId).isNotNull();
    }

    @Test
    @DisplayName("Статус 200: Тест на получение сущности")
    @DataSet(value = "datasets/commentToLikeRepositoryTest/commentLike.yml", cleanAfter = true, cleanBefore = true)
    void findCommentToLike() {
        CommentToLikes commentToLike = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "CommentToLikesEntity.json", CommentToLikes.class);

        CommentToLikes savedCommentToLike = commentToLikesRepository.save(commentToLike);
        UUID commentToLikeId = savedCommentToLike.getId();

        Optional<CommentToLikes> findCommentToLike = commentToLikesRepository.findById(commentToLikeId);
        assertThat(findCommentToLike).isPresent();
        assertThat(findCommentToLike.get()).isEqualTo(savedCommentToLike);
    }

    @Test
    @DisplayName("Статус 200: Тест на удаление сущности")
    @DataSet(value = "datasets/commentToLikeRepositoryTest/commentLike.yml", cleanAfter = true, cleanBefore = true)
    void deleteCommentToLike() {
        CommentToLikes commentToLike = jsonParserUtil.getObjectFromJson(TASK_SERVICE_ENTITY + "CommentToLikesEntity.json", CommentToLikes.class);

        CommentToLikes savedCommentToLike = commentToLikesRepository.save(commentToLike);
        UUID commentToLikeId = savedCommentToLike.getId();

        commentToLikesRepository.delete(savedCommentToLike);

        Optional<CommentToLikes> findCommentToLike = commentToLikesRepository.findById(commentToLikeId);
        assertThat(findCommentToLike).isNotPresent();
    }

    @Test
    @DisplayName("Статус 200: Тест на подсчет количества записей лайков для комментария")
    @DataSet(value = "datasets/commentToLikeRepositoryTest/commentLike.yml", cleanAfter = true, cleanBefore = true)
    void countCommentToLikesByCommentId() {
        UUID commentId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        int count = commentToLikesRepository.countByCommentId(commentId);

        assertThat(count).isEqualTo(1);
    }
}
