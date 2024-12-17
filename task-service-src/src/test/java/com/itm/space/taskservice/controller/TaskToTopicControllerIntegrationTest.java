package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.itm.space.taskservice.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.itm.space.taskservice.api.constant.ApiConstants.TASK_PATH;

class TaskToTopicControllerIntegrationTest extends BaseIntegrationTest {

    private final String CORRECT_TOPIC_ID = "97b608a9-302e-4387-a5f4-fd35969cdc21";

    private final String INCORRECT_TOPIC_ID = "97b608a9-302e-4387-a5f4";

    private final String NOTFOUND_TOPIC_ID = "97b608a9-302e-4387-a5f4-fd35969cdc23";

    private final String CORRECT_USERNAME = "viktorivanov64035@gmail.com";

    @Test
    @DataSet(value = "datasets/controller/taskToTopicController/ListTasksByTopicId.yml", cleanAfter = true)
    @DisplayName("Тест на успешное получение списка задач")
    void shouldGetListTasks200() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(TASK_PATH)
                        .queryParam("topicId", CORRECT_TOPIC_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.[0].id").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f78")
                .jsonPath("$.[0].title").isEqualTo("string")
                .jsonPath("$.[0].text").isEqualTo("string")
                .jsonPath("$.[0].attachments").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f08")
                .jsonPath("$.[0].archived").isEqualTo(false)
                .jsonPath("$.[0].solutions.[0].id").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f08")
                .jsonPath("$.[0].solutions.[0].text").isEqualTo("string")
                .jsonPath("$.[0].solutions.[0].correct").isEqualTo(true)
                .jsonPath("$.[1].id").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f79")
                .jsonPath("$.[1].title").isEqualTo("string")
                .jsonPath("$.[1].text").isEqualTo("string")
                .jsonPath("$.[1].attachments").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f09")
                .jsonPath("$.[1].archived").isEqualTo(false)
                .jsonPath("$.[1].solutions.[0].id").isEqualTo("497f6eca-6276-4993-bfeb-53cbbbba6f09")
                .jsonPath("$.[1].solutions.[0].text").isEqualTo("string")
                .jsonPath("$.[1].solutions.[0].correct").isEqualTo(true)
                .jsonPath("$.[2]").doesNotExist();
    }

    @Test
    @DataSet(value = "datasets/controller/taskToTopicController/ListTasksByTopicId.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400, неверный запрос")
    void shouldReturn400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(TASK_PATH)
                        .queryParam("topicId", INCORRECT_TOPIC_ID)
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
    @DisplayName("Должен вернуть 401, не авторизованный пользователь")
    void shouldReturn401NotAuthorized() {
        webTestClient.get()
                .uri(TASK_PATH)
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
    @DataSet(value = "datasets/controller/taskToTopicController/ListTasksByTopicId.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 404, topicId не найден")
    void shouldReturn404InvalidId() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(TASK_PATH)
                        .queryParam("topicId", NOTFOUND_TOPIC_ID)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(CORRECT_USERNAME))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Тема для получения задач не найдена");
    }
}