package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.itm.space.itmplatformcommonmodels.kafka.TaskCommentEvent;
import com.itm.space.taskservice.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static com.itm.space.taskservice.api.constant.ApiConstants.STUDENT_DISLIKE_COMMENTS;

class DislikeUserControllerIntegrationTest extends BaseIntegrationTest {

    @Value("${spring.kafka.topic.task-comment-events}")
    private String topic;

    @Test
    @DisplayName("Статус 200: тело ответа на получение дизлайка от другого пользователя")
    @DataSet(value = "datasets/controller/CommentUserController/CorrectDislikeComment.yml", cleanAfter = true)
    @WithMockUser(username = "ad634490-b8bb-4b20-b639-d162015097c3", roles = "STUDENT")
    void getDislike200() {
        UUID commentId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        webTestClient.patch()
                .uri(STUDENT_DISLIKE_COMMENTS + "/" + commentId + "/dislike")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123e4567-e89b-12d3-a456-426614174000")
                .jsonPath("$.text").isEqualTo("Pupupu")
                .jsonPath("$.authorName").isEqualTo("Kiki")
                .jsonPath("$.actual").isEqualTo(true)
                .jsonPath("$.taskId").isEqualTo("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .jsonPath("$.like").isEqualTo(0)
                .jsonPath("$.dislike").isEqualTo(2)
                .jsonPath("$.moderated").isEqualTo(true)
                .jsonPath("$.deleted").isEqualTo(false)
                .jsonPath("$.parentId").isEqualTo("70850378-7d3c-4f45-91b7-942d4dfbbd43")
                .jsonPath("$.authorId").isEqualTo("550e8400-e29b-41d4-a716-446655440000");

        TaskCommentEvent expectedEvent = jsonParserUtil
                .getObjectFromJson("json/kafka/producer/DislikesProducer.json", TaskCommentEvent.class);
        testConsumerService.consumeAndValidate(topic, expectedEvent);
    }

    @Test
    @DisplayName("Статус 400: тело ответа на плохой запрос")
    @DataSet(value = "datasets/controller/CommentUserController/CorrectDislikeComment.yml", cleanAfter = true)
    @WithMockUser(roles = "STUDENT")
    void getDislike400() {
        UUID commentId = UUID.randomUUID();
        webTestClient.patch()
                .uri(STUDENT_DISLIKE_COMMENTS + "/" + commentId + "/dislike")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DisplayName("Статус 401: Должен вернуть 401, если пользователь не аутентифицирован")
    void getDislike401() {
        UUID commentId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        webTestClient.patch()
                .uri(STUDENT_DISLIKE_COMMENTS + "/" + commentId + "/dislike")
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не аутентифицирован");
    }

    @Test
    @DisplayName("Статус 403: тело ответа на получение дизлайка от роли пользователя у которой недостаточно прав")
    @DataSet(value = "datasets/controller/CommentUserController/CorrectDislikeComment.yml", cleanAfter = true)
    @WithMockUser(roles = "MODERATOR")
    void getDislike403() {
        UUID commentId = UUID.fromString("f3a64766-7891-467b-97f3-0c5a2bfb5bcb");
        webTestClient.patch()
                .uri(STUDENT_DISLIKE_COMMENTS + "/" + commentId + "/dislike")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.FORBIDDEN.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Недостаточно прав");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/CorrectDislikeComment.yml", cleanAfter = true)
    @DisplayName("Статус 404: Должен вернуть 404, комментарий не найден")
    @WithMockUser(username = "69f1e0d6-7e0a-4f7b-9f5f-9f5f9f5f9f5f", roles = "STUDENT")
    void getDislike404() {
        UUID commentId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        webTestClient.patch()
                .uri(STUDENT_DISLIKE_COMMENTS + "/" + commentId + "/dislike")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Комментарий не найден");
    }

    @Test
    @DisplayName("Статус 200: Успешное удаление дизлайка")
    @DataSet(value = "datasets/controller/CommentUserController/CorrectDislikeComment.yml", cleanAfter = true)
    @WithMockUser(username = "d8753bf8-7999-4226-b1d6-8d16a23166c2", roles = "STUDENT")
    void succesfullDeletedDislikes() {
        UUID commentId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        webTestClient.patch()
                .uri(STUDENT_DISLIKE_COMMENTS + "/" + commentId + "/dislike")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123e4567-e89b-12d3-a456-426614174000")
                .jsonPath("$.text").isEqualTo("Pupupu")
                .jsonPath("$.authorName").isEqualTo("Kiki")
                .jsonPath("$.actual").isEqualTo(true)
                .jsonPath("$.taskId").isEqualTo("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .jsonPath("$.like").isEqualTo(0)
                .jsonPath("$.dislike").isEqualTo(2)
                .jsonPath("$.moderated").isEqualTo(true)
                .jsonPath("$.deleted").isEqualTo(false)
                .jsonPath("$.parentId").isEqualTo("70850378-7d3c-4f45-91b7-942d4dfbbd43")
                .jsonPath("$.authorId").isEqualTo("550e8400-e29b-41d4-a716-446655440000");

        webTestClient.patch()
                .uri(STUDENT_DISLIKE_COMMENTS + "/" + commentId + "/dislike")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("123e4567-e89b-12d3-a456-426614174000")
                .jsonPath("$.text").isEqualTo("Pupupu")
                .jsonPath("$.authorName").isEqualTo("Kiki")
                .jsonPath("$.actual").isEqualTo(true)
                .jsonPath("$.taskId").isEqualTo("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .jsonPath("$.like").isEqualTo(0)
                .jsonPath("$.dislike").isEqualTo(1)
                .jsonPath("$.moderated").isEqualTo(true)
                .jsonPath("$.deleted").isEqualTo(false)
                .jsonPath("$.parentId").isEqualTo("70850378-7d3c-4f45-91b7-942d4dfbbd43")
                .jsonPath("$.authorId").isEqualTo("550e8400-e29b-41d4-a716-446655440000");

        TaskCommentEvent expectedEvent = jsonParserUtil
                .getObjectFromJson("json/kafka/producer/DislikesProducerRemoved.json", TaskCommentEvent.class);
        testConsumerService.consumeAndValidate(topic, expectedEvent);
    }

    @Test
    @DisplayName("Статус 400: Пользователь не может поставить дизлайк своему собственному комментарию")
    @DataSet(value = "datasets/controller/CommentUserController/CorrectDislikeComment.yml", cleanAfter = true)
    @WithMockUser(username = "550e8400-e29b-41d4-a716-446655440000", roles = "STUDENT")
    void cannotDislikeOwnComment() {
        UUID commentId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        webTestClient.patch()
                .uri(STUDENT_DISLIKE_COMMENTS + "/" + commentId + "/dislike")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не может ставить себе дизлайк");
    }
}
