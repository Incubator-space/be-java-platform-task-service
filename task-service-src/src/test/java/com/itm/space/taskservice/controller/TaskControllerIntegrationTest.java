package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static com.itm.space.taskservice.api.constant.ApiConstants.TASK_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON;


class TaskControllerIntegrationTest extends BaseIntegrationTest {

    private final String CORRECT_TASK_ID = "e6e9d88a-9b63-468a-aec3-b7a11de27af8";

    private final String NOTFOUND_TASK_ID = "97b608a9-302e-4387-a5f4-fd35969cdc22";

    private final String CORRECT_USERNAME = "viktorivanov64035@gmail.com";


    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Тест на успешное получение задачи")
    void getTaskByIdSuccess200() {

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(TASK_PATH + "/{id}")
                        .build(CORRECT_TASK_ID))
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(CORRECT_TASK_ID)
                .jsonPath("$.title").isEqualTo("string")
                .jsonPath("$.text").isEqualTo("string")
                .jsonPath("$.topicId").isEqualTo("97b608a9-302e-4387-a5f4-fd35969cdc21")
                .jsonPath("$.attachments").isArray()
                .jsonPath("$.attachments[0]").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f08")
                .jsonPath("$.archived").isEqualTo(true)
                .jsonPath("$.solutions[0].id").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f08")
                .jsonPath("$.solutions[0].taskId").isEqualTo("e6e9d88a-9b63-468a-aec3-b7a11de27af8")
                .jsonPath("$.solutions[0].text").isEqualTo("string")
                .jsonPath("$.solutions[0].correct").isEqualTo(true);
    }

    @Test
    @DisplayName("Должен вернуть 401, если пользователь не аутентифицирован")
    void shouldReturnNotAuthorized401() {

        webTestClient.get()
                .uri(TASK_PATH)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Пользователь не аутентифицирован");
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 404, задача не найдена")
    void shouldGetTaskNotFound404() {

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(TASK_PATH + "/{id}")
                        .build(NOTFOUND_TASK_ID))
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Задача не найдена.");
    }
}
