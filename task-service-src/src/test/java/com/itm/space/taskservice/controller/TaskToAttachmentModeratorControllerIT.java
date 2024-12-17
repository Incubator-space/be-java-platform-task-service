package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.api.constant.RoleConstants;
import com.itm.space.taskservice.api.request.TaskToAttachmentModeratorRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static com.itm.space.taskservice.api.constant.ApiConstants.MODERATOR_ATTACHMENT_PATH;

public class TaskToAttachmentModeratorControllerIT extends BaseIntegrationTest {

    private final String testAttachmentId = "789e0123-4567-89ab-cdef-0123456789ac";

    private final String testUsernameWithRoleAdmin = "admin@admin.ru";

    private final String testUsernameWithRoleStudent = "student@student.ru";

    private final TaskToAttachmentModeratorRequest testTaskToAttachmentAddRequest = jsonParserUtil.getObjectFromJson(
            "json/controller/taskToAttachmentModeratorRequest.json",
            TaskToAttachmentModeratorRequest.class);

    private final TaskToAttachmentModeratorRequest testTaskIdIncorrectRequest = jsonParserUtil.getObjectFromJson(
            "json/controller/taskToAttacmentIdIncorrectRequest.json",
            TaskToAttachmentModeratorRequest.class);

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DataSet(value = {
            "datasets.taskToAttachmentRepositoryTest/TaskToAttachmentModerator.yml"},
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = {
            "datasets.taskToAttachmentRepositoryTest/TaskToAttachmentModeratorExpected.yml"},
            ignoreCols = {"created"})
    @DisplayName("Тест: возвращается статус 200, аттачмент успешно добавлен к задаче")
    void shouldReturnStatus200IfTaskToAttachmentAdd() {
        webTestClient.post()
                .uri(MODERATOR_ATTACHMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testTaskToAttachmentAddRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.attachmentId").isEqualTo(testAttachmentId);
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 400, неправильные аргументы запроса")
    void shouldReturnStatus400IfIllegalRequestArguments() {
        webTestClient.post()
                .uri(MODERATOR_ATTACHMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .bodyValue(new TaskToAttachmentModeratorRequest())
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
                .uri(MODERATOR_ATTACHMENT_PATH)
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
                .uri(MODERATOR_ATTACHMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .bodyValue(testTaskToAttachmentAddRequest)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo("Forbidden")
                .jsonPath("$.message").isEqualTo("Недостаточно прав");
    }

    @Test
    @WithMockUser(authorities = {RoleConstants.ADMIN, RoleConstants.MODERATOR})
    @DisplayName("Тест: возвращается статус 404, задача не найдена")
    void shouldReturnStatus404IfTaskNotFound() {
        webTestClient.post()
                .uri(MODERATOR_ATTACHMENT_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testTaskIdIncorrectRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Задача не найдена");
    }

    @Test
    @DataSet(value = {
            "datasets.taskToAttachmentRepositoryTest/TaskToDelete.yml"},
            cleanAfter = true, cleanBefore = true)
    @ExpectedDataSet(value = {
            "datasets.taskToAttachmentRepositoryTest/TaskToDeletedExample.yml"},
            ignoreCols = {"created"})
    @DisplayName("Тест: возвращается статус 200, аттачмент удален")
    void testTaskToAttachmentDelete() {
        webTestClient.delete()
                .uri(MODERATOR_ATTACHMENT_PATH + "?attachmentId=789e0123-4567-89ab-cdef-0123456789ac")
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.attachmentId").isEqualTo("789e0123-4567-89ab-cdef-0123456789ac");
    }

    @Test
    @DisplayName("Тест: возвращается статус 400, неправильные аргументы запроса")
    void testReturnStatus400DeleteIfTaskNotFound() {
        webTestClient.delete()
                .uri(MODERATOR_ATTACHMENT_PATH + "?attachmentId=12345")
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DisplayName("Тест: возвращается статус 401, если пользователь не аутентифицирован")
    void testReturnStatus401WhenNotAuthorized() {
        webTestClient.delete()
                .uri(MODERATOR_ATTACHMENT_PATH + "?attachmentId=789e0123-4567-89ab-cdef-0123456789ac")
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
    void testReturnStatus403IfAccessForbidden() {
        webTestClient.delete()
                .uri(MODERATOR_ATTACHMENT_PATH + "?attachmentId=789e0123-4567-89ab-cdef-0123456789ac")
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo("Forbidden")
                .jsonPath("$.message").isEqualTo("Недостаточно прав");
    }

    @Test
    @DisplayName("Тест: возвращается статус 404, задача не найдена")
    void testReturnStatus404IfTaskNotFound() {
        webTestClient.delete()
                .uri(MODERATOR_ATTACHMENT_PATH + "?attachmentId=789e0123-4567-89ab-cdef-0123456788ac")
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleAdmin))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Аттачмент для удаления не найден");
    }
}