package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.itmplatformcommonmodels.kafka.TaskCommentEvent;
import com.itm.space.itmplatformcommonmodels.kafka.enums.TaskCommentType;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.api.constant.ApiConstants;
import com.itm.space.taskservice.api.request.CreateCommentRequest;
import com.itm.space.taskservice.api.request.UpdateCommentRequest;
import com.itm.space.taskservice.api.response.CommentUserResponse;
import com.itm.space.taskservice.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static com.itm.space.taskservice.api.constant.ApiConstants.USER_COMMENT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentUserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    protected CommentRepository commentRepository;

    @Value("${spring.kafka.topic.task-comment-events}")
    private String topic;

    private final String CORRECT_COMMENT_PATH = "dataset/controller/CommentUserController/CorrectComment.yml";

    private final String INCORRECT_COMMENT_PATH = "dataset/controller/CommentUserController/IncorrectComment.yml";

    private final String CORRECT_COMMENT_REQUEST_PATH = "json/service/CorrectCommentRequest.json";

    private final String INCORRECT_ID_COMMENT_REQUEST_PATH = "json/service/IncorrectIdCommentRequest.json";

    private final String INCORRECT_TEXT_COMMENT_REQUEST_PATH = "json/service/IncorrectTextCommentRequest.json";

    private final String STATUS_400_COMMENT_REQUEST_PATH = "json/service/Status400CommentRequest.json";

    private final String STATUS_400_COMMENT_WHEN_NOT_VALID_REGEX = "json/service/Status400CommentNotValidRegex.json";

    private final String CORRECT_COMMENT_ID = "f34348d9-8a0b-4e95-8ea4-ff3b8e387c4a";

    private final String CORRECT_TASK_ID = "f47ac10b-58cc-4372-a567-0e02b2c3d479";

    private final String INCORRECT_TASK_CORRECT_ID = "b3b29ef2-6b0d-4cc7-a9f8-acee2ef3c117";

    private final String INCORRECT_TASK_INCORRECT_ID = "ya zadachya";

    private final String INCORRECT_TASK_ID_NUll = null;

    private final String CORRECT_USERNAME = "viktorivanov64035@gmail.com";

    private final String CORRECT_USER_ID = "968ab300-5711-4a2d-941e-4a74a2ba0aab";

    private final String CORRECT_URL_TO_DELETE = USER_COMMENT_PATH + "?commentId=";

    private final String CORRECT_COMMENT_ID_DELETED_FALSE = "4ac48468-184b-4520-899b-9177aa4b4010";

    private final String CORRECT_DELETE_USERNAME = "viktorivanov64035@gmail.com";

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true,
            cleanBefore = true)
    @DisplayName("Создание нового комментария с обязательными полями")
    void shouldCreateCommentAndReturnCommentBody() {

        CreateCommentRequest request = jsonParserUtil.getObjectFromJson(
                "json/service/CommentService/createRequest/CorrectCommentCreateRequestWithAllParams.json",
                CreateCommentRequest.class);

        CommentUserResponse response = webTestClient.post()
                .uri(USER_COMMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentUserResponse.class).consumeWith(result -> {
                    CommentUserResponse responseBody = result.getResponseBody();
                    assertNotNull(responseBody);
                    assertNotNull(responseBody.getId());
                    assertEquals(request.getParentId() != null ? request.getParentId().toString() : null, responseBody.getParentId().toString());
                    assertEquals(request.getText(), responseBody.getText());
                    assertEquals(request.getTaskId().toString(), responseBody.getTaskId().toString());
                    assertEquals(CORRECT_USERNAME, responseBody.getAuthorName());
                }).returnResult().getResponseBody();

        testConsumerService.consumeAndValidate(topic, new TaskCommentEvent(
                response.getId(),
                response.getAuthorId(),
                UUID.fromString("968ab300-5711-4a2d-941e-4a74a2ba0aab"),
                response.getParentId(),
                UUID.fromString("968ab300-5711-4a2d-941e-4a74a2ba0aab"),
                response.getText(),
                TaskCommentType.CREATED)
        );
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Создание нового комментария со 2 aттачментами")
    void shouldCreateCommentRequestWithTwoAttachments() {

        CreateCommentRequest request = jsonParserUtil.getObjectFromJson(
                "json/service/CommentService/createRequest/CorrectCommentCreateRequestWithAllParams.json",
                CreateCommentRequest.class);

        CommentUserResponse response = webTestClient.post()
                .uri(USER_COMMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentUserResponse.class).consumeWith(result -> {
                    CommentUserResponse responseBody = result.getResponseBody();
                    assertNotNull(responseBody);
                    assertNotNull(responseBody.getId());
                    assertEquals(request.getParentId() != null ? request.getParentId().toString() : null, responseBody.getParentId().toString());
                    assertEquals(request.getText(), responseBody.getText());
                    assertEquals(request.getTaskId().toString(), responseBody.getTaskId().toString());
                    assertEquals(CORRECT_USERNAME, responseBody.getAuthorName());
                }).returnResult().getResponseBody();

        testConsumerService.consumeAndValidate(topic, new TaskCommentEvent(
                response.getId(),
                response.getAuthorId(),
                UUID.fromString("968ab300-5711-4a2d-941e-4a74a2ba0aab"),
                response.getParentId(),
                UUID.fromString("968ab300-5711-4a2d-941e-4a74a2ba0aab"),
                response.getText(),
                TaskCommentType.CREATED)
        );
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Создание нового комментария со всеми полями")
    void shouldCreateCommentRequestWithAllParams() {

        CreateCommentRequest request = jsonParserUtil.getObjectFromJson(
                "json/service/CommentService/createRequest/CorrectCommentCreateRequestAll.json",
                CreateCommentRequest.class);

        CommentUserResponse response = webTestClient.post()
                .uri(USER_COMMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentUserResponse.class).consumeWith(result -> {
                    CommentUserResponse responseBody = result.getResponseBody();
                    assertNotNull(responseBody);
                    assertNotNull(responseBody.getId());
                    assertEquals(request.getParentId() != null ? request.getParentId().toString() : null, responseBody.getParentId().toString());
                    assertEquals(request.getText(), responseBody.getText());
                    assertEquals(request.getTaskId().toString(), responseBody.getTaskId().toString());
                    assertEquals(CORRECT_USERNAME, responseBody.getAuthorName());
                }).returnResult().getResponseBody();

        testConsumerService.consumeAndValidate(topic, new TaskCommentEvent(
                response.getId(),
                response.getAuthorId(),
                UUID.fromString("968ab300-5711-4a2d-941e-4a74a2ba0aab"),
                response.getParentId(),
                UUID.fromString("968ab300-5711-4a2d-941e-4a74a2ba0aab"),
                response.getText(),
                TaskCommentType.CREATED)
        );


    }

    @ParameterizedTest(name = "Путь к проверяемому json: {arguments}")
    @CsvSource({
            "json/service/CommentService/createRequest/IncorrectCommentCreateRequestWithNullText.json",
            "json/service/CommentService/createRequest/IncorrectCommentCreateRequestWithBlankText.json",
            "json/service/CommentService/createRequest/IncorrectCommentCreateRequestWithTextBlankSpace.json"
    })
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть статус код 400, при создании нового комментария с некорректным текстом комментария")
    void shouldGetStatusCode400IfCommentTextIsInvalidWhenCreatingComment(String jsonPath) {

        CreateCommentRequest request = jsonParserUtil.getObjectFromJson(
                jsonPath
                , CreateCommentRequest.class);

        webTestClient.post()
                .uri(USER_COMMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть статус код 400, при создании нового комментария, если в коллекции attachments больше 3х объектов")
    void shouldGetStatusCode400IfAttachmentsMoreThanThreeItems() {

        CreateCommentRequest request = jsonParserUtil.getObjectFromJson(
                "json/service/CommentService/createRequest/IncorrectCommentCreateRequestWithFourAttachments.json"
                , CreateCommentRequest.class);

        webTestClient.post()
                .uri(USER_COMMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть статус код 404, при создании нового комментария к несуществующей задаче")
    void shouldGetStatusCode404IfTaskIdDoesNotExist() {

        CreateCommentRequest request = jsonParserUtil.getObjectFromJson(
                "json/service/CommentService/createRequest/IncorrectCommentCreateRequestWithIncorrectTaskId.json"
                , CreateCommentRequest.class);

        webTestClient.post()
                .uri(USER_COMMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Задача не найдена");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть статус код 404, при создании нового комментария с указанием несуществующей " +
            "родительской задачи")
    void shouldGetStatusCode404IfParentIdDoesNotExist() {

        CreateCommentRequest request = jsonParserUtil.getObjectFromJson(
                "json/service/CommentService/createRequest/IncorrectCommentCreateRequestWithIncorrectParentId.json"
                , CreateCommentRequest.class);

        webTestClient.post()
                .uri(USER_COMMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Родительская задача не найдена");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Тело ответа на получение дефолтной страницы комментариев к задаче")
    void shouldGetComments200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_COMMENT_PATH)
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .queryParam("taskId", CORRECT_TASK_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2))
                .jsonPath("$.[0].id").isEqualTo("5724a7e9-6f2a-497f-9e89-1c08cb5e3b9d")
                .jsonPath("$.[0].taskId").isEqualTo("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .jsonPath("$.[0].moderated").isEqualTo(true)
                .jsonPath("$.[1].id").isEqualTo("f34348d9-8a0b-4e95-8ea4-ff3b8e387c4a")
                .jsonPath("$.[1].taskId").isEqualTo("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .jsonPath("$.[1].moderated").isEqualTo(true);
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Запрос второй страницы когда в бд записи только на первой (должен быть пустой список)")
    void shouldGetCommentsPage2Empty() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_COMMENT_PATH)
                        .queryParam("page", "1")
                        .queryParam("size", "6")
                        .queryParam("taskId", CORRECT_TASK_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(0))
                .jsonPath("$").isEmpty();
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Запрос второй страницы когда 2 записи а размер страницы например 1 (в ответе должно быть 1 запись)")
    void shouldGetBankCardsPage2With1() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_COMMENT_PATH)
                        .queryParam("page", "1")
                        .queryParam("size", "1")
                        .queryParam("taskId", CORRECT_TASK_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$")
                .isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$.[0].id").isEqualTo("f34348d9-8a0b-4e95-8ea4-ff3b8e387c4a")
                .jsonPath("$.[0].taskId").isEqualTo("f47ac10b-58cc-4372-a567-0e02b2c3d479")
                .jsonPath("$.[0].moderated").isEqualTo(true);
    }

    @Test
    @DisplayName("Должен вернуть 401, если пользователь не аутентифицирован")
    void shouldReturn401WhenNotAuthorized_401() {
        webTestClient.get()
                .uri(USER_COMMENT_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не аутентифицирован");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 404, задача, к которой запрошены комментарии, не найдена")
    void shouldGetCommentsPage404() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_COMMENT_PATH)
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .queryParam("taskId", INCORRECT_TASK_CORRECT_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Задача, к которой запрошены комментарии, не найдена");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400, неправильные аргументы запроса")
    void shouldGetCommentsPage400WhenIncorrectTaskId() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_COMMENT_PATH)
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .queryParam("taskId", INCORRECT_TASK_INCORRECT_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400, неправильные аргументы запроса")
    void shouldGetCommentsPage400WhenTaskIdIsNull() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_COMMENT_PATH)
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .queryParam("taskId", INCORRECT_TASK_ID_NUll)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400, неверный запрос")
    void shouldGetCommentsPage400WhenPageNotValid() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_COMMENT_PATH)
                        .queryParam("page", "ноль")
                        .queryParam("size", "10")
                        .queryParam("taskId", CORRECT_TASK_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DataSet(value = "datasets/controller/CommentUserController/Correct2Comments.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400, неверный запрос, ошибка в поле size")
    void shouldGetCommentsPage400WhenSizeNotValid() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_COMMENT_PATH)
                        .queryParam("page", "0")
                        .queryParam("size", "десять")
                        .queryParam("taskId", CORRECT_TASK_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DisplayName("Тест обновления комментария")
    @DataSet(value = CORRECT_COMMENT_PATH, cleanAfter = true)
    void shouldUpdateComment() {
        UpdateCommentRequest request =
                jsonParserUtil.getObjectFromJson(CORRECT_COMMENT_REQUEST_PATH, UpdateCommentRequest.class);

        webTestClient.put()
                .uri(ApiConstants.USER_COMMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentUserResponse.class)
                .value(response -> {
                    assertThat(response.getId()).isEqualTo(UUID.fromString(CORRECT_COMMENT_ID));
                    assertThat(response.getText()).isEqualTo("Test is True , and have @ many symbols ! 7");
                    assertThat(response.getAuthorName()).isEqualTo("Nikita");
                    assertThat(response.isActual()).isTrue();
                    assertThat(response.getTaskId()).isEqualTo(UUID.fromString(CORRECT_TASK_ID));
                    assertThat(response.getLike()).isZero();
                    assertThat(response.getDislike()).isZero();
                    assertThat(response.isModerated()).isTrue();
                    assertThat(response.isDeleted()).isFalse();
                    assertThat(response.getParentId()).isNull();
                    assertThat(response.getAuthorId()).isEqualTo(UUID.fromString(CORRECT_USER_ID));
                });

        TaskCommentEvent event = jsonParserUtil
                .getObjectFromJson(
                        "json/controller/taskModeratorControllerTest/CorrectTaskCommentEvent.json",
                        TaskCommentEvent.class
                );
        testConsumerService.consumeAndValidate(topic, event);
    }

    @Test
    @DisplayName("Тест на ошибку 400 при пустых кавичек коментарий ")
    @DataSet(value = CORRECT_COMMENT_PATH, cleanAfter = true)
    void shouldUpdateCommentBadRequest() {
        UpdateCommentRequest request =
                jsonParserUtil.getObjectFromJson(INCORRECT_TEXT_COMMENT_REQUEST_PATH, UpdateCommentRequest.class);

        webTestClient.put()
                .uri(ApiConstants.USER_COMMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");

                    }

    @Test
    @DisplayName("Тест на ошибку 401")
    @DataSet(value = CORRECT_COMMENT_PATH, cleanAfter = true)
    void shouldUpdateCommentUnauthorized() {
        UpdateCommentRequest request =
                jsonParserUtil.getObjectFromJson(CORRECT_COMMENT_REQUEST_PATH, UpdateCommentRequest.class);

        webTestClient.put()
                .uri(ApiConstants.USER_COMMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не аутентифицирован");
    }

    @Test
    @DisplayName("Тест на ошибку 403")
    @DataSet(value = INCORRECT_COMMENT_PATH, cleanAfter = true)
    void shouldUpdateCommentFalse() {
        UpdateCommentRequest request =
                jsonParserUtil.getObjectFromJson(CORRECT_COMMENT_REQUEST_PATH, UpdateCommentRequest.class);

        webTestClient.put()
                .uri(ApiConstants.USER_COMMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.FORBIDDEN.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Вы не можете редактировать не свой комментарий");
    }

    @Test
    @DisplayName("Тест на ошибку 404")
    @DataSet(value = CORRECT_COMMENT_PATH, cleanAfter = true)
    void shouldUpdateCommentNotFound() {
        UpdateCommentRequest request =
                jsonParserUtil.getObjectFromJson(INCORRECT_ID_COMMENT_REQUEST_PATH, UpdateCommentRequest.class);

        webTestClient.put()
                .uri(ApiConstants.USER_COMMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Комментарий не найден");
    }

    @Test
    @DisplayName("Тест на ошибку 400 при значений коментарий = null")
    @DataSet(value = CORRECT_COMMENT_PATH, cleanAfter = true)
    void shouldUpdateCommentBadRequestError() {

        UpdateCommentRequest request =
                jsonParserUtil.getObjectFromJson(STATUS_400_COMMENT_REQUEST_PATH, UpdateCommentRequest.class);

        webTestClient.put()
                .uri(ApiConstants.USER_COMMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DisplayName("Тест на ошибку 400 при коментарий с текстом меньше 10 букв")
    @DataSet(value = CORRECT_COMMENT_PATH, cleanAfter = true)
    void shouldUpdateCommentWhenNotValidRegex() {
        UpdateCommentRequest request =
                jsonParserUtil.getObjectFromJson(STATUS_400_COMMENT_WHEN_NOT_VALID_REGEX, UpdateCommentRequest.class);

        webTestClient.put()
                .uri(ApiConstants.USER_COMMENT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DisplayName("Тест на смену признака deleted на true")
    @DataSet(value = "datasets/controller/CommentUserController/CommentsDelete.yml", cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "datasets/controller/CommentUserController/ExpectedCommentsDelete.yml", compareOperation = CompareOperation.CONTAINS, ignoreCols = "updated")
    void shouldDeleteComment() {
        UUID commentId = UUID.fromString("4ac48468-184b-4520-899b-9177aa4b4010");

        webTestClient.delete()
                .uri(CORRECT_URL_TO_DELETE + commentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_DELETE_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("4ac48468-184b-4520-899b-9177aa4b4010")
                .jsonPath("$.text").isEqualTo("sad")
                .jsonPath("$.authorName").isEqualTo("dimas")
                .jsonPath("$.actual").isEqualTo("true")
                .jsonPath("$.taskId").isEqualTo("8194617e-7e54-493c-bf9e-e675d5b5c0e6")
                .jsonPath("$.like").isEqualTo(0)
                .jsonPath("$.dislike").isEqualTo(0)
                .jsonPath("$.moderated").isEqualTo("true")
                .jsonPath("$.deleted").isEqualTo("true")
                .jsonPath("$.parentId").isEqualTo("70850378-7d3c-4f45-91b7-942d4dfbbd43")
                .jsonPath("$.authorId").isEqualTo("968ab300-5711-4a2d-941e-4a74a2ba0aab");
    }

    @Test
    @DisplayName("Тест на ошибку 400 при попытке удалить уже удаленный комментарий")
    @DataSet(value = "datasets/controller/CommentUserController/CommentsDelete.yml", cleanAfter = true, cleanBefore = true)
    void shouldDeleteCommentError_400() {
        UUID commentId = UUID.fromString("4ac48468-184b-4520-899b-9177aa4b4010");

        webTestClient.delete()
                .uri(CORRECT_URL_TO_DELETE + commentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_DELETE_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        webTestClient.delete()
                .uri(CORRECT_URL_TO_DELETE + commentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_DELETE_USERNAME))
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
    @DataSet(value = "datasets/controller/CommentUserController/CommentsDelete.yml", cleanAfter = true, cleanBefore = true)
    void shouldSoftDeleteCommentError_401() {

        UUID commentId = UUID.fromString(CORRECT_COMMENT_ID_DELETED_FALSE);

        webTestClient.delete()
                .uri(CORRECT_URL_TO_DELETE + commentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не аутентифицирован");
    }

    @Test
    @DisplayName("Тест на ошибку 403 при попытке изменить признак deleted на true у чужого комментария")
    @DataSet(value = "datasets/controller/CommentUserController/CommentsDelete.yml", cleanAfter = true, cleanBefore = true)
    void shouldDeleteCommentError_403() {

        UUID commentId = UUID.fromString("9de55b88-a0b3-499f-9cb0-0c541432f0c2");

        webTestClient.delete()
                .uri(CORRECT_URL_TO_DELETE + commentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.FORBIDDEN.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Вы не можете удалить не свой комментарий");
    }

    @Test
    @DisplayName("Тест на ошибку 404 удалить несуществующий комментарий")
    @DataSet(value = "datasets/controller/CommentUserController/CommentsDelete.yml", cleanAfter = true, cleanBefore = true)
    void shouldDeleteCommentError_404() {
        UUID nonExistentCommentId = UUID.randomUUID();

        webTestClient.delete()
                .uri(CORRECT_URL_TO_DELETE + nonExistentCommentId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_DELETE_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Комментарий не найден");
    }
}