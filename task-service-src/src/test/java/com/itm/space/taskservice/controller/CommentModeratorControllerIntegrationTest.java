package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.itmplatformcommonmodels.kafka.TaskCommentEvent;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.api.constant.RoleConstants;
import com.itm.space.taskservice.api.request.ModerateCommentRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.itm.space.taskservice.api.constant.ApiConstants.MODERATE_COMMENT_PATH;

class CommentModeratorControllerIntegrationTest extends BaseIntegrationTest {

    @Value("${spring.kafka.topic.task-comment-events}")
    private String topic;

    private final String CORRECT_URL_TO_MODERATE = MODERATE_COMMENT_PATH + "/accept";

    private final String CORRECT_URL_TO_SOFT_DELETE = MODERATE_COMMENT_PATH + "?commentId=";

    private final String CORRECT_COMMENT_ID = "497f6eca-6276-4993-bfeb-53cbbbba6f08";

    private final String CORRECT_COMMENT_ID_DELETED_FALSE = "4ac48468-184b-4520-899b-9177aa4b4010";

    private final String CORRECT_COMMENT_ID_DELETED_TRUE= "9de55b88-a0b3-499f-9cb0-0c541432f0c2";

    private final String INCORRECT_COMMENT_ID_DELETED= "4ac48468-184b-4520-899b-9177aa4b4011";

    private final String CORRECT_COMMENT_REQUEST_PATH = "json/service/CommentService/CorrectModerateCommentRequest.json";

    private final String INCORRECT_COMMENT_REQUEST_PATH_EMPTY = "json/service/CommentService/IncorrectModerateCommentRequestEmpty.json";

    private final String INCORRECT_COMMENT_REQUEST_PATH_NULL = "json/service/CommentService/IncorrectModerateCommentRequestNull.json";

    private final String INCORRECT_COMMENT_REQUEST_PATH_BAD = "json/service/CommentService/IncorrectModerateCommentRequestBad.json";

    private final String CORRECT_USERNAME = "moder@moder.ru";

    private final String INCORRECT_USERNAME = "viktorivanov64035@gmail.com";

    @Test
    @DisplayName("Тест на смену признака deleted на true")
    @DataSet(value = "datasets/controller/CommentModeratorController/CommentsForDelete.yml", cleanAfter = true)
    void shouldSoftDeleteComment() {

        UUID commentId = UUID.fromString(CORRECT_COMMENT_ID_DELETED_FALSE);

        webTestClient.delete()
                .uri(CORRECT_URL_TO_SOFT_DELETE + commentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("4ac48468-184b-4520-899b-9177aa4b4010")
                .jsonPath("$.text").isEqualTo("sad")
                .jsonPath("$.authorName").isEqualTo("dimas")
                .jsonPath("$.actual").isEqualTo("true")
                .jsonPath("$.taskId").isEqualTo("8194617e-7e54-493c-bf9e-e675d5b5c0e6")
                .jsonPath("$.like").isEqualTo("0")
                .jsonPath("$.dislike").isEqualTo("0")
                .jsonPath("$.moderated").isEqualTo("true")
                .jsonPath("$.deleted").isEqualTo("true")
                .jsonPath("$.parentId").isEqualTo("70850378-7d3c-4f45-91b7-942d4dfbbd43")
                .jsonPath("$.authorId").isEqualTo("b3b29ef2-6b0d-4cc7-a9f8-acee2ef3c117");

        TaskCommentEvent event = jsonParserUtil
                .getObjectFromJson(
                        "json/controller/taskModeratorControllerTest/CorrectSoftDeleteCommentEvent.json",
                        TaskCommentEvent.class
                );
        testConsumerService.consumeAndValidate(topic, event);
    }

    @Test
    @DisplayName("Тест на ошибку 400 при попытке изменить признак deleted на true у удаленного ранее комментария")
    @DataSet(value = "datasets/controller/CommentModeratorController/CommentsForDelete.yml", cleanAfter = true)
    void shouldSoftDeleteCommentError_400() {

        UUID commentId = UUID.fromString(CORRECT_COMMENT_ID_DELETED_TRUE);

        webTestClient.delete()
                .uri(CORRECT_URL_TO_SOFT_DELETE + commentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Комментарий уже удален");
    }

    @Test
    @DisplayName("Тест на ошибку 401 при попытке изменить признак deleted на true комментария у неавторизованного пользователя")
    @DataSet(value = "datasets/controller/CommentModeratorController/CommentsForDelete.yml", cleanAfter = true)
    void shouldSoftDeleteCommentError_401() {

        UUID commentId = UUID.fromString(CORRECT_COMMENT_ID_DELETED_FALSE);

        webTestClient.delete()
                .uri(CORRECT_URL_TO_SOFT_DELETE + commentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не аутентифицирован");
    }

    @Test
    @DisplayName("Тест на ошибку 403 при попытке изменить признак deleted на true комментария у пользователя без роли Moderator")
    @DataSet(value = "datasets/controller/CommentModeratorController/CommentsForDelete.yml", cleanAfter = true)
    void shouldSoftDeleteCommentError_403() {

        UUID commentId = UUID.fromString(CORRECT_COMMENT_ID_DELETED_FALSE);

        webTestClient.delete()
                .uri(CORRECT_URL_TO_SOFT_DELETE + commentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(INCORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.FORBIDDEN.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Недостаточно прав");
    }

    @Test
    @DisplayName("Тест на ошибку 404 при попытке изменить признак deleted на true у комментария, которого нет в БД")
    @DataSet(value = "datasets/controller/CommentModeratorController/CommentsForDelete.yml", cleanAfter = true)
    void shouldSoftDeleteCommentError_404() {

        UUID commentId = UUID.fromString(INCORRECT_COMMENT_ID_DELETED);

        webTestClient.delete()
                .uri(CORRECT_URL_TO_SOFT_DELETE + commentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Комментарий не найден");
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.MODERATOR})
    @DataSet(value = "datasets/controller.CommentModeratorController/CommentToModerate.yml", cleanAfter = true)
    @ExpectedDataSet(value = "datasets/controller.CommentModeratorController/CommentModeratedExpected.yml")
    @DisplayName("Тело ответа на запрос, возвращающее id комментария отмеченного как прошедшего модерацию")
    void shouldModerateComment() {

        ModerateCommentRequest request =
                jsonParserUtil.getObjectFromJson(CORRECT_COMMENT_REQUEST_PATH, ModerateCommentRequest.class);

        webTestClient.put()
                .uri(CORRECT_URL_TO_MODERATE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ModerateCommentRequest.class)
                .value(response -> {
                    Assertions.assertThat(response.getCommentIds())
                            .contains(UUID.fromString(CORRECT_COMMENT_ID));
                });
    }

    @Test
    @DataSet(value = "datasets/controller.CommentModeratorController/CommentToModerate.yml", cleanAfter = true)
    @DisplayName("Тест на некорректный запрос - пустой запрос, HttpStatus 400")
    void shouldReturnBadRequest_1() {

        ModerateCommentRequest request =
                jsonParserUtil.getObjectFromJson(INCORRECT_COMMENT_REQUEST_PATH_EMPTY, ModerateCommentRequest.class);

        webTestClient.put()
                .uri(CORRECT_URL_TO_MODERATE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @DataSet(value = "datasets/controller.CommentModeratorController/CommentToModerate.yml", cleanAfter = true)
    @DisplayName("Тест на некорректный запрос - null, HttpStatus 400")
    void shouldReturnBadRequest_2() {

        ModerateCommentRequest request =
                jsonParserUtil.getObjectFromJson(INCORRECT_COMMENT_REQUEST_PATH_NULL, ModerateCommentRequest.class);

        webTestClient.put()
                .uri(CORRECT_URL_TO_MODERATE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @DataSet(value = "datasets/controller.CommentModeratorController/CommentToModerate.yml", cleanAfter = true)
    @DisplayName("Тест на некорректный запрос - символы, HttpStatus 400")
    void shouldReturnBadRequest_3() {

        webTestClient.put()
                .uri(CORRECT_URL_TO_MODERATE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                            {
                            "commentIds" : "#@!(*&^"
                            }
                """)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @DisplayName("Тест на ошибку авторизации ,HttpStatus 401")
    void shouldReturnUnauthorized() {

        ModerateCommentRequest request =
                jsonParserUtil.getObjectFromJson(CORRECT_COMMENT_REQUEST_PATH, ModerateCommentRequest.class);

        webTestClient.put()
                .uri(CORRECT_URL_TO_MODERATE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }


    @Test
    @DisplayName("Тест на отсутствие данных ,HttpStatus 404")
    void shouldReturnNotFound() {

        ModerateCommentRequest request = new ModerateCommentRequest();
        request.setCommentIds(Collections.singletonList(UUID.randomUUID()));

        webTestClient.put()
                .uri(CORRECT_URL_TO_MODERATE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Один или несколько комментариев не найдены в БД");
    }

    @Test
    @DisplayName("Тест на отсутствие прав ,HttpStatus 403")
    void shouldReturnForbidden() {

        ModerateCommentRequest request =
                jsonParserUtil.getObjectFromJson(CORRECT_COMMENT_REQUEST_PATH, ModerateCommentRequest.class);

        webTestClient.put()
                .uri(CORRECT_URL_TO_MODERATE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(INCORRECT_USERNAME))
                .exchange()
                .expectStatus().isForbidden();
    }
}