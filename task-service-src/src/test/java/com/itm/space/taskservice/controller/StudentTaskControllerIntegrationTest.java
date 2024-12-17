package com.itm.space.taskservice.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.itm.space.itmplatformcommonmodels.kafka.TaskEvent;
import com.itm.space.taskservice.BaseIntegrationTest;
import com.itm.space.taskservice.api.request.CompleteTaskRequestByStudent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class StudentTaskControllerIntegrationTest extends BaseIntegrationTest {

    @Value("${spring.kafka.topic.task-events}")
    private String topic;

    private final String testUsernameWithRoleStudent = "student@student.ru";

    public static final String STUDENT_TASK_COMPLETE_PATH = "/api/student/tasks/complete";

    private final CompleteTaskRequestByStudent correctTaskCompleteRequestWithSolution = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/taskCompleteRequests/CorrectRequestWithSolution.json",
            CompleteTaskRequestByStudent.class);

    private final CompleteTaskRequestByStudent correctTaskCompleteRequestWithSolutionIds = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/taskCompleteRequests/CorrectRequestWithSolutionIds.json",
            CompleteTaskRequestByStudent.class);

    @AfterEach
    void afterEach() {
        WireMock.reset();
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Тест на успешное завершение задачи при передаче в запросе правильного ответа в поле solutionIds")
    void completeTaskByStudentWithSuccess200IfCorrectSolutionId() {

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(correctTaskCompleteRequestWithSolutionIds)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result").isEqualTo(true)
                .jsonPath("$.failureReason").isEqualTo("");

        TaskEvent expectedEvent = jsonParserUtil
                .getObjectFromJson("json/kafka/producer/TaskEventProducerForStudentСompletion.json", TaskEvent.class);
        testConsumerService.consumeAndValidate(topic, expectedEvent);
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Тест на неуспешное завершение задачи и выброс исключения связанного с отправкой в кафку при передаче в запросе неправильного ответа в поле solutionIds")
    void completeTaskByStudentWithFailIfIncorrectSolutionId() {

        CompleteTaskRequestByStudent incorrectTaskCompleteRequestWithFalseSolutionId = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithSolutionIds.json",
                CompleteTaskRequestByStudent.class);

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(incorrectTaskCompleteRequestWithFalseSolutionId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Непредвиденная ошибка в процессе проверки");
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Должен вернуть статус код 400, если при завершении задачи передано id решения, " +
                 "не принадлежащего к указанной задаче")
    void shouldReturn400IfSolutionIdDoesNotBelongTask() {

        CompleteTaskRequestByStudent completeTaskRequestWithSolutionId = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithSolutionIdsFromAnotherTask.json",
                CompleteTaskRequestByStudent.class);

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(completeTaskRequestWithSolutionId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message")
                .isEqualTo("Указанное решение с ID " + completeTaskRequestWithSolutionId.getSolutionIds()
                        .get(1) + " не принадлежит задаче с ID " + completeTaskRequestWithSolutionId.getTaskId());
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Должен вернуть статус код 400, если при завершении задачи отсутствует решение")
    void shouldReturn400IfSolutionsDontExist() {

        CompleteTaskRequestByStudent completeTaskRequestWithSolutionId = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithoutSolutions.json",
                CompleteTaskRequestByStudent.class);

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(completeTaskRequestWithSolutionId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message")
                .isEqualTo("Неправильные аргументы запроса: Решение не приложено");
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Должен вернуть статус код 400, если при завершении задачи присутствуют и решение и айди решений")
    void shouldReturn400IfSolutionAndSolutionIdsArePresent() {

        CompleteTaskRequestByStudent completeTaskRequestWithSolutionId = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithSolutionAndSolutionIds.json",
                CompleteTaskRequestByStudent.class);

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(completeTaskRequestWithSolutionId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message")
                .isEqualTo("Неправильные аргументы запроса");
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("При закрытии задачи должен вернуть true, если адаптер chatGPT возвращает результат CORRECT")
    void shouldReturnFalseResponseIfSolutionIsCorrect() {

        String stringExpectedResponse = jsonParserUtil.getStringFromJson
                ("json/service/taskService/taskCompleteRequests/responsesFromChatGPT/CorrectResponse.json");

        stubFor(post(urlEqualTo("/task/check"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(stringExpectedResponse)));

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(correctTaskCompleteRequestWithSolution)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.result").isEqualTo(true)
                .jsonPath("$.failureReason").isEqualTo("");

        TaskEvent expectedEvent = jsonParserUtil
                .getObjectFromJson("json/kafka/producer/TaskEventProducerForStudentСompletion.json", TaskEvent.class);
        testConsumerService.consumeAndValidate(topic, expectedEvent);
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("При ответе 500 от адаптер chatGPT, будут повторятся запросы еще несколько раз")
    void shouldRepeatRequestsIfServerReturnStatus500() {

        stubFor(post(urlEqualTo("/task/check"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(correctTaskCompleteRequestWithSolution)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Внутренняя ошибка сервера");

        verify(exactly(4), postRequestedFor(urlEqualTo("/task/check")));
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("При закрытии задачи с неверным решением должен вернуть ошибку связанную с отправкой в кафку, если адаптер chatGPT возвращает результат INCORRECT")
    void shouldReturnFalseResponseIfSolutionIsIncorrect() {

        String stringExpectedResponse = jsonParserUtil.getStringFromJson
                ("json/service/taskService/taskCompleteRequests/responsesFromChatGPT/IncorrectResponse.json");

        stubFor(post(urlEqualTo("/task/check"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(stringExpectedResponse)));

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(correctTaskCompleteRequestWithSolution)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Непредвиденная ошибка в процессе проверки");
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("При закрытии задачи должен вернуть false, если адаптер chatGPT возвращает результат INVALID")
    void shouldReturn400IfSolutionIsInvalid() {

        String stringExpectedResponse = jsonParserUtil.getStringFromJson
                ("json/service/taskService/taskCompleteRequests/responsesFromChatGPT/InvalidResponse.json");

        stubFor(post(urlEqualTo("/task/check"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(stringExpectedResponse)));

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(correctTaskCompleteRequestWithSolution)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Отправлены некорректные данные");
    }

    @Test
    @DisplayName("Должен вернуть 401, если пользователь не аутентифицирован")
    void shouldReturnNotAuthorized401() {

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .accept(APPLICATION_JSON)
                .bodyValue(correctTaskCompleteRequestWithSolution)
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
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization("admin@admin.ru"))
                .bodyValue(correctTaskCompleteRequestWithSolution)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.type").isEqualTo("Forbidden")
                .jsonPath("$.message").isEqualTo("Недостаточно прав");
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 404, если задача не найдена")
    void shouldGetTaskNotFound404IfTaskIdNotFound() {

        CompleteTaskRequestByStudent incorrectTaskCompleteRequestWithIncorrectTaskId = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithIncorrectTaskId.json",
                CompleteTaskRequestByStudent.class
        );

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(incorrectTaskCompleteRequestWithIncorrectTaskId)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Задача для завершения не найдена");
    }

    @Test
    @DataSet(value = "datasets/controllerToTaskControllerTest/CorrectTask.yml", cleanAfter = true)
    @DisplayName("Должен вернуть 400, если номер задачи отсутствует")
    void shouldGet400IfTaskIdNull() {

        CompleteTaskRequestByStudent incorrectTaskCompleteRequestWithIncorrectTaskId = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithoutTaskId.json",
                CompleteTaskRequestByStudent.class
        );

        webTestClient.post()
                .uri(STUDENT_TASK_COMPLETE_PATH)
                .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization(testUsernameWithRoleStudent))
                .accept(APPLICATION_JSON)
                .bodyValue(incorrectTaskCompleteRequestWithIncorrectTaskId)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.type").isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .jsonPath("$.message").isEqualTo("Неправильные аргументы запроса");
    }
}
