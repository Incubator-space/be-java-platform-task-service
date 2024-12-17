package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.api.constant.RoleConstants;
import com.itm.space.taskservice.api.request.TaskRequest;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.UUID;

import static com.itm.space.taskservice.api.constant.ApiConstants.MODERATOR_TASK_BASE_PATH;

class TaskModeratorControllerIntegrationTest extends BaseIntegrationTest {

    private final String testTitle = "1.1.2 Spring Boot";

    private final String testText = "Это текст задачи про Spring Boot";

    private final String testTopicId = "97b608a9-302e-4387-a5f4-fd35969cdc21";

    private final String testUsernameWithRoleAdmin = "admin@admin.ru";

    private final String testUsernameWithRoleStudent = "student@student.ru";

    private final UUID CORRECT_RESPONSE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final String INVALID_TOPIC_ID = "97b608a9-ЫУЦЮ-4387-a5f4-fd35969cdc20";

    private final String INVALID_ATTACHMENT_ID = "467fcefa-2e40-4fce-ba73-045647553ЗЮК";

    private final UpdateTaskRequest mockUpdateRequest = UpdateTaskRequest.builder()
            .title("updated title")
            .text("updated text")
            .attachments(Collections.emptyList())
            .solutions(Collections.emptyList())
            .build();

    private final TaskRequest testTaskRequest = jsonParserUtil.getObjectFromJson(
            "json/controller/taskModeratorControllerTest/CorrectTaskRequest.json",
            TaskRequest.class
    );

    private final TaskRequest testTaskRequestWithAllField = jsonParserUtil.getObjectFromJson(
            "json/controller/taskModeratorControllerTest/CorrectTaskRequestWithAllField.json",
            TaskRequest.class
    );

    private final TaskRequest testTaskRequestWithNonExistTopic = jsonParserUtil.getObjectFromJson(
            "json/controller/taskModeratorControllerTest/CorrectTaskRequestWithNonExistTopic.json",
            TaskRequest.class
    );

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DataSet(value = {
            "datasets/controller/TaskModeratorController/task.yml"},
            cleanAfter = true, cleanBefore = true
    )
    @ExpectedDataSet(value = {
            "datasets/controller/TaskModeratorController/taskExpected.yml"},
            ignoreCols = {"task_id", "created", "created_by", "updated", "updated_by"}
    )
    @DisplayName("Тест: возвращается статус 200, задача успешно создана")
    void shouldReturnStatus200IfTaskCreated() {
        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testTaskRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo(testTitle)
                .jsonPath("$.text").isEqualTo(testText)
                .jsonPath("$.topicId").isEqualTo(testTopicId)
                .jsonPath("$.attachments").isEmpty()
                .jsonPath("$.solutions").isEmpty();
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DataSet(value = {
            "datasets/controller/TaskModeratorController/task.yml"},
            cleanAfter = true, cleanBefore = true
    )
    @ExpectedDataSet(value = {
            "datasets/controller/TaskModeratorController/taskWithAllFieldExpected.yml"},
            ignoreCols = {"task_id", "created", "created_by", "updated", "updated_by"}
    )
    @DisplayName("Тест: возвращается статус 200, задача со всеми полями успешно создана")
    void shouldReturnStatus200IfTaskCreatedWithAllField() {
        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testTaskRequestWithAllField)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo(testTitle)
                .jsonPath("$.text").isEqualTo(testText)
                .jsonPath("$.topicId").isEqualTo(testTopicId)
                .jsonPath("$.attachments[0]").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f08")
                .jsonPath("$.solutions[0].text").isEqualTo("string")
                .jsonPath("$.solutions[0].correct").isEqualTo(true);
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, неправильные аргументы запроса")
    void shouldReturnStatus400IfIllegalRequestArguments() {
        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .bodyValue(new TaskRequest())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DisplayName("Тест: возвращается статус 401, если пользователь не аутентифицирован")
    void shouldReturnStatus401WhenNotAuthorized() {
        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
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
    @DisplayName("Тест: возвращается статус 403, недостаточно прав")
    void shouldReturnStatus403IfAccessForbidden() {
        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .bodyValue(testTaskRequest)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo("Forbidden")
                .jsonPath("$.message").isEqualTo("Недостаточно прав");
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 404, тема для создания задачи не найдена")
    void shouldReturnStatus404IfTaskToTopicNotFound() {
        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testTaskRequestWithNonExistTopic)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Тема для создания задачи не найдена");
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DataSet(value = "/datasets/controller/TaskModeratorController/task.yml",
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = "datasets/controller/TaskModeratorController/taskExpectedToUpdate.yml",
            ignoreCols = {"updated", "updated_by", "created", "created_by"})
    @DisplayName("Тест: возвращается статус 200, успешное обновление таска")
    void shouldReturnStatus200IfTaskUpdated() {
        webTestClient.put()
                .uri(MODERATOR_TASK_BASE_PATH + "/" + CORRECT_RESPONSE_ID)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(mockUpdateRequest), UpdateTaskRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("updated title")
                .jsonPath("$.text").isEqualTo("updated text")
                .jsonPath("$.topicId").isEqualTo(testTopicId)
                .jsonPath("$.id").isEqualTo(CORRECT_RESPONSE_ID.toString())
                .jsonPath("$.archived").isEqualTo(false)
                .jsonPath("$.attachments").isEmpty()
                .jsonPath("$.solutions").isEmpty();
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DataSet(value = "/datasets/controller/TaskModeratorController/task.yml",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("Тест: возвращается статус 400, неправильные параметры запроса")
    void shouldReturnStatus400IfIllegalRequestArgumentsForUpdate() {
        webTestClient.put()
                .uri(MODERATOR_TASK_BASE_PATH + "/" + CORRECT_RESPONSE_ID)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateTaskRequest())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DataSet(value = "/datasets/controller/TaskModeratorController/task.yml",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("Тест: возвращается статус 401, пользователь не аутентифицирован")
    void shouldReturnStatus401WhenNotAuthorizedForUpdate() {
        webTestClient.put()
                .uri(MODERATOR_TASK_BASE_PATH + "/" + CORRECT_RESPONSE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(mockUpdateRequest), UpdateTaskRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не аутентифицирован");
    }

    @Test
    @DataSet(value = "/datasets/controller/TaskModeratorController/task.yml",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("Тест: возвращается статус 403, недостаточно прав")
    void shouldReturnStatus403IfAccessForbiddenForUpdate() {
        webTestClient.put()
                .uri(MODERATOR_TASK_BASE_PATH + "/" + CORRECT_RESPONSE_ID)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(mockUpdateRequest), UpdateTaskRequest.class)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo("Forbidden")
                .jsonPath("$.message").isEqualTo("Недостаточно прав");
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DataSet(value = "/datasets/controller/TaskModeratorController/task.yml",
            cleanAfter = true, cleanBefore = true)
    @DisplayName("Тест: возвращается статус 404, задача для обновления не найдена")
    void shouldReturnStatus404IfTaskForUpdateNotFound() {
        webTestClient.put()
                .uri(MODERATOR_TASK_BASE_PATH + "/" + UUID.randomUUID())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(mockUpdateRequest), UpdateTaskRequest.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Задача для обновления не найдена");
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400 при некорректных полях запроса для обновления задачи")
    void shouldReturnStatus400IfInvalidFieldsInUpdateTaskRequest() {

        UpdateTaskRequest invalidUpdateTaskRequest = UpdateTaskRequest.builder()
                .title("InvalidTitle123!")
                .text("")
                .build();

        UUID testTaskId = UUID.randomUUID();

        webTestClient.put()
                .uri(MODERATOR_TASK_BASE_PATH + "/" + testTaskId)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .bodyValue(invalidUpdateTaskRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @Tag("TestGroupPRJ1568")
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, если поле title пустое при создании задачи")
    void shouldReturnStatus400ifTitleIsEmpty() {

        TaskRequest taskRequest = TaskRequest.builder()
                .title("")
                .text("Some valid text")
                .topicId(UUID.randomUUID())
                .build();

        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(taskRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @Tag("TestGroupPRJ1568")
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, если поле title содержит цифры при создании задачи")
    void shouldReturnStatus400ifTitleIsNumber() {

        TaskRequest taskRequest = TaskRequest.builder()
                .title("123456")
                .text("Some valid text")
                .topicId(UUID.randomUUID())
                .build();

        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(taskRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @Tag("TestGroupPRJ1568")
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, если поле title содержит спецсимволы при создании задачи")
    void shouldReturnStatus400ifTitleIsSpecCharacters() {

        TaskRequest taskRequest = TaskRequest.builder()
                .title("№;\"!()?:")
                .text("Some valid text")
                .topicId(UUID.randomUUID())
                .build();

        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(taskRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @Tag("TestGroupPRJ1568")
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, если поле text пустое при создании задачи")
    void shouldReturnStatus400ifTextIsEmpty() {

        TaskRequest taskRequest = TaskRequest.builder()
                .text("")
                .topicId(UUID.randomUUID())
                .build();

        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(taskRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @Tag("TestGroupPRJ1568")
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, если поле text содержит спецсимволы при создании задачи")
    void shouldReturnStatus400ifTextIsSpecCharacters() {

        TaskRequest taskRequest = TaskRequest.builder()
                .text("№;\"!()?:")
                .topicId(UUID.randomUUID())
                .build();

        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(taskRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @Tag("TestGroupPRJ1568")
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, если поле TopicId пустое при создании задачи")
    void shouldReturnStatus400ifTopicIdIsEmpty() {

        TaskRequest taskRequest = TaskRequest.builder()
                .topicId(null)
                .build();

        webTestClient.post()
                .uri(MODERATOR_TASK_BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(taskRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @Tag("TestGroupPRJ1568")
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, если поле TopicId не валидный при создании задачи")
    void shouldReturnStatus400ifTopicIdIsInvalid() {

        TaskRequest taskRequest = TaskRequest.builder()
                .title("Valid title")
                .text("Valid text")
                .build();

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(MODERATOR_TASK_BASE_PATH)
                        .queryParam("topicId", INVALID_TOPIC_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(taskRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }
}
