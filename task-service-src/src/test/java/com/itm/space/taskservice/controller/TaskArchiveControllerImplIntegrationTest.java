package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.api.constant.RoleConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static com.itm.space.taskservice.api.constant.ApiConstants.TASK_ARCHIVED_PATH;

class TaskArchiveControllerImplIntegrationTest extends BaseIntegrationTest {

    private static final String INVALID_PARAMS = "\"№;%?-:%;\"№?)(*?(%;№";

    private final String TASK_NON_ARCHIVED = "88c8399e-8ea0-11ed-a5c8-0242ac120002";

    private final String TASK_NON_ARCHIVED_ID = "6f96984c-8e9b-11ed-a8a9-0242ac120002";

    private final String USERNAME = "admin@admin.ru";

    private final String INCORRECT_USERNAME = "student@student.ru";

    @Test
    @DisplayName("Ответит статусом 200 и вернёт информацию о заархивированной задаче")
    @WithMockUser(authorities = {RoleConstants.MODERATOR, RoleConstants.ADMIN})
    @DataSet(value = "datasets/controllerToTaskControllerTest/taskArchiveControllerTest.yml")
    @ExpectedDataSet(value = "datasets/controllerToTaskControllerTest/taskArchiveControllerTest1.yml",
            ignoreCols = {"updated", "updated_by"})
    void archiveTaskResponse() {

        webTestClient.delete()
                .uri(TASK_ARCHIVED_PATH + "/" + TASK_NON_ARCHIVED)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(TASK_NON_ARCHIVED)
                .jsonPath("$.title").isEqualTo("stringTitle")
                .jsonPath("$.text").isEqualTo("string")
                .jsonPath("$.topicId").isEqualTo("b3b29ef2-6b0d-4cc7-a9f8-acee2ef3c876")
                .jsonPath("$.attachments[0]").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f78")
                .jsonPath("$.archived").isEqualTo(true)
                .jsonPath("$.solutions[0].id").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f08")
                .jsonPath("$.solutions[0].taskId").isEqualTo(TASK_NON_ARCHIVED)
                .jsonPath("$.solutions[0].text").isEqualTo("string")
                .jsonPath("$.solutions[0].correct").isEqualTo(false);
    }

    @Test
    @DisplayName("Ответит статусом 400 - задача заархивирована. Получение вариантов решения недоступно.")
    @DataSet(value = "datasets/controllerToTaskControllerTest/taskArchiveControllerTest1.yml", cleanAfter = true)
    void return400WhenTaskArchived() {

        webTestClient.delete()
                .uri(TASK_ARCHIVED_PATH + "/" + TASK_NON_ARCHIVED)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Задача уже заархивирована.");
    }

    @Test
    @DisplayName("Ответит статусом 400. Неправильные параметры запроса")
    void return400WhenInvalidRequestParams() {

        webTestClient.delete()
                .uri(TASK_ARCHIVED_PATH + "/" + INVALID_PARAMS)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DisplayName("Ответит статусом 401 - пользователь не аутентифицирован")
    @DataSet(value = "datasets/controllerToTaskControllerTest/taskArchiveControllerTest.yml")
    void archiveTaskNotAuthenticated() {

        webTestClient.get()
                .uri(TASK_ARCHIVED_PATH)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не аутентифицирован");
    }

    @Test
    @DisplayName("Ответит статусом 403 - недостаточно прав")
    @DataSet(value = "datasets/controllerToTaskControllerTest/taskArchiveControllerTest.yml")
    void returnNotEnoughRights() {

        webTestClient.delete()
                .uri(TASK_ARCHIVED_PATH + "/" + TASK_NON_ARCHIVED)
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
    @DisplayName("Ответит статусом 404 - задача не найдена")
    void taskResponseNotFound() {

        webTestClient.delete()
                .uri(TASK_ARCHIVED_PATH + "/" + TASK_NON_ARCHIVED_ID)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Задача не найдена.");
    }
}


