package com.itm.space.taskservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.entity.CommentToDislikes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentToDislikesRepositoryTest extends BaseIntegrationTest {

    @Autowired
    CommentToDislikesRepository commentToDislikesRepository;

    @Test
    @DisplayName("Тест на сохранение сущности")
    @DataSet(value = "datasets/commentToDislikeRepositoryTest/commentDislike.yml", cleanAfter = true, cleanBefore = true)
    void saveCommentToDislike() {
        CommentToDislikes commentToDislike = jsonParserUtil.getObjectFromJson("json/entity/CommentToDislikesEntity.json", CommentToDislikes.class);

        commentToDislike.setCreated(LocalDateTime.now());
        commentToDislike.setCreatedBy(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        assertThat(commentToDislike.getCreated()).isNotNull();
        assertThat(commentToDislike.getCreatedBy()).isNotNull();

        CommentToDislikes savedCommentToDislike = commentToDislikesRepository.save(commentToDislike);

        UUID commentToDislikeId = savedCommentToDislike.getId();
        assertThat(commentToDislikeId).isNotNull();
    }

    @Test
    @DisplayName("Тест на получение сущности")
    @DataSet(value = "datasets/commentToDislikeRepositoryTest/commentDislike.yml", cleanAfter = true, cleanBefore = true)
    void findCommentToDislike() {
        CommentToDislikes commentToDislike = jsonParserUtil.getObjectFromJson("json/entity/CommentToDislikesEntity.json", CommentToDislikes.class);

        CommentToDislikes savedCommentToDislike = commentToDislikesRepository.save(commentToDislike);
        UUID commentToDislikeId = savedCommentToDislike.getId();

        Optional<CommentToDislikes> findCommentToDislike = commentToDislikesRepository.findById(commentToDislikeId);
        assertThat(findCommentToDislike).isPresent();
        assertThat(findCommentToDislike.get()).isEqualTo(savedCommentToDislike);
    }

    @Test
    @DisplayName("Тест на удаление сущности")
    @DataSet(value = "datasets/commentToDislikeRepositoryTest/commentDislike.yml", cleanAfter = true, cleanBefore = true)
    void deleteCommentToDislike() {
        CommentToDislikes commentToDislike = jsonParserUtil.getObjectFromJson("json/entity/CommentToDislikesEntity.json", CommentToDislikes.class);

        CommentToDislikes savedCommentToDislike = commentToDislikesRepository.save(commentToDislike);
        UUID commentToDislikeId = savedCommentToDislike.getId();

        commentToDislikesRepository.delete(savedCommentToDislike);

        Optional<CommentToDislikes> findCommentToDislike = commentToDislikesRepository.findById(commentToDislikeId);
        assertThat(findCommentToDislike).isNotPresent();
    }

    @Test
    @DisplayName("Тест на подсчет количества записей дизлайков для комментария")
    @DataSet(value = "datasets/commentToDislikeRepositoryTest/commentDislike.yml", cleanAfter = true, cleanBefore = true)
    void countCommentToDislikesByCommentId() {
        UUID commentId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        int count = commentToDislikesRepository.countByCommentId(commentId);

        assertThat(count).isEqualTo(1);
    }
}
