package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.api.request.ChatGPTTaskCheckRequest;
import com.itm.space.taskservice.api.request.TaskSolutionDTO;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.response.ChatGPTTaskCheckResponse;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskCompletionResponse;
import com.itm.space.taskservice.entity.SolutionToTask;
import com.itm.space.taskservice.entity.TaskToUser;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.SolutionToTaskMapper;
import com.itm.space.taskservice.repository.SolutionToTaskRepository;
import com.itm.space.taskservice.repository.TaskToUserRepository;
import com.itm.space.taskservice.service.impl.ChatGptAdapterClientService;
import com.itm.space.taskservice.service.impl.SolutionToTaskServiceImpl;
import com.itm.space.taskservice.util.SecurityUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolutionToTaskServiceTest extends BaseUnitTest {

    @Mock
    private SolutionToTaskRepository solutionToTaskRepository;

    @Mock
    private SolutionToTaskMapper solutionToTaskMapper;

    @InjectMocks
    private SolutionToTaskServiceImpl solutionImpl;

    @Mock
    private TaskToUserRepository taskToUserRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private ChatGptAdapterClientService chatGptAdapterClient;

    ChatGPTTaskCheckRequest correctRequestToChatGPT = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/taskCompleteRequests/requestsToChatGPT/CorrectRequestToChatGPT.json",
            ChatGPTTaskCheckRequest.class);

    @Test
    @DisplayName("Возвращает список решений по ID задачи")
    void getSolutionByTaskId() {

        UUID taskId = UUID.fromString("497f6eca-6276-4993-bfeb-53cbbbba6f08");

        SolutionToTask solutionTest = jsonParserUtil.getObjectFromJson(
                "json/TaskService/TrueSolutions.json", SolutionToTask.class);
        SolutionToTaskResponse expectedSolutionResponse = jsonParserUtil.getObjectFromJson(
                "json/TaskService/ResponseSolutions.json", SolutionToTaskResponse.class);

        when(solutionToTaskRepository.findSolutionsByTaskId(taskId)).thenReturn(List.of(solutionTest));
        when(solutionToTaskMapper.toSolutionToTaskResponse(solutionTest)).thenReturn(expectedSolutionResponse);

        List<SolutionToTaskResponse> response = solutionImpl.findSolutionsByTaskId(taskId);

        assertEquals(expectedSolutionResponse, response.get(0));
    }

    @Test
    @DisplayName("Не найдено решений для задачи")
    void getSolutionByTaskIdNull() {

        UUID taskId = UUID.fromString("88c8399e-8ea0-11ed-a5c8-0242ac120987");

        when(solutionToTaskRepository.findSolutionsByTaskId(taskId)).thenReturn(Collections.emptyList());

        List<SolutionToTaskResponse> responses = solutionImpl.findSolutionsByTaskId(taskId);
        assertTrue(responses.isEmpty());

    }

    @Test
    @DisplayName("Сохраняет все решения для задачи")
    void saveAllSolutionToTask() {

        SolutionToTask testSolutionToTask = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/CorrectSolutionToTask.json",
                SolutionToTask.class
        );

        List<SolutionToTask> testSolutionToTaskList = List.of(testSolutionToTask);

        solutionImpl.saveAll(testSolutionToTaskList);

        verify(solutionToTaskRepository).saveAll(testSolutionToTaskList);
    }

    @Test
    @DisplayName("Успешное обновление решений")
    void updateSolutionToTask() {

        UUID testTaskId = UUID.randomUUID();

        SolutionToTaskResponse testSolutionToTaskResponse = new SolutionToTaskResponse(
                UUID.randomUUID(), testTaskId, "string", true);

        TaskSolutionDTO testTaskSolutionDTO = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/CorrectSolutionToTaskRequest.json",
                TaskSolutionDTO.class);

        UpdateTaskRequest testUpdateTaskRequestWithAllField = jsonParserUtil.getObjectFromJson(
                "json/controller/taskModeratorControllerTest/CorrectUpdateTaskRequestWithAllFields.json",
                UpdateTaskRequest.class);

        SolutionToTask solution = new SolutionToTask();
        List<SolutionToTask> solutions = List.of(solution);

        List<SolutionToTaskResponse> expectedResponses = List.of(testSolutionToTaskResponse);

        when(solutionToTaskMapper.toSolutionToTask(testTaskId, testTaskSolutionDTO))
                .thenReturn(solution);

        when(solutionToTaskMapper.toSolutionToTaskResponse(solution))
                .thenReturn(testSolutionToTaskResponse);

        when(solutionToTaskRepository.saveAll(anyList())).thenReturn(solutions);

        List<SolutionToTaskResponse> actualResponse =
                solutionImpl.updateSolutionToTask(testUpdateTaskRequestWithAllField, testTaskId);

        Assert.assertEquals(expectedResponses, actualResponse);
    }

    @Test
    @DisplayName("Должен вернуть в ответе true, если при завершении задачи было передано solutionId, у которого " +
            "результат - true")
    void shouldReturnTrueInResponseIfSolutionIdHasTrueResult() {

        List<SolutionToTaskResponse> taskSolutions = jsonParserUtil.getListFromJson(
                "json/service/taskService/taskCompleteRequests/CorrectListOfSolutionToTaskResponses.json",
                SolutionToTaskResponse.class);

        List<UUID> studentSolutionIds = Collections.singletonList(taskSolutions.get(0).getId());
        UUID taskId = taskSolutions.get(0).getTaskId();

        TaskCompletionResponse response = solutionImpl.checkSolutionIds(studentSolutionIds, taskSolutions, taskId);

        assertEquals(true, response.getResult());
        assertEquals("", response.getFailureReason());
        verify(taskToUserRepository, times(1)).save(any(TaskToUser.class));
    }

    @Test
    @DisplayName("Должен вернуть в ответе false, если при завершении задачи было передано solutionId, у которого " +
            "результат - false")
    void shouldReturnFalseInResponseIfSolutionIdHasFalseResult() {

        List<SolutionToTaskResponse> taskSolutions = jsonParserUtil.getListFromJson(
                "json/service/taskService/taskCompleteRequests/CorrectListOfSolutionToTaskResponses.json",
                SolutionToTaskResponse.class);
        ;

        List<UUID> studentSolutionIds = Collections.singletonList(taskSolutions.get(1).getId());
        UUID taskId = taskSolutions.get(0).getTaskId();

        TaskCompletionResponse response = solutionImpl.checkSolutionIds(studentSolutionIds, taskSolutions, taskId);

        assertEquals(false, response.getResult());
        assertEquals("Указанное решение с ID " + taskSolutions.get(1).getId() + " неверно.", response.getFailureReason());
        verify(taskToUserRepository, never()).save(any(TaskToUser.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если при завершении задачи передано id решения, " +
            "не принадлежащего к указанной задаче")
    void shouldThrowTaskExceptionIfSolutionIdDoesNotBelongTask() {

        List<SolutionToTaskResponse> taskSolutions = jsonParserUtil.getListFromJson(
                "json/service/taskService/taskCompleteRequests/CorrectListOfSolutionToTaskResponses.json",
                SolutionToTaskResponse.class);

        List<UUID> studentSolutionIds = Collections.singletonList(UUID.randomUUID());
        UUID taskId = taskSolutions.get(0).getTaskId();

        TaskException exception = assertThrows(TaskException.class, () -> solutionImpl.checkSolutionIds(studentSolutionIds, taskSolutions, taskId));
        Assertions.assertTrue(exception.getMessage().contains("Указанное решение с ID") &&
                exception.getMessage().contains("не принадлежит задаче с ID"));
        verify(taskToUserRepository, never()).save(any(TaskToUser.class));
    }

    @Test
    @DisplayName("Должен вернуть в ответе true, если было передано верное решение в поле solution")
    void shouldReturnTrueInResponseIfSolutionIsCorrect() {

        ChatGPTTaskCheckResponse exceptedResponse = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/responsesFromChatGPT/CorrectResponse.json",
                ChatGPTTaskCheckResponse.class);

        when(chatGptAdapterClient.sendRequestToChatGPT(any())).thenReturn(exceptedResponse);

        TaskCompletionResponse taskCompletionResponse = solutionImpl.getAnswerFromChatGPT(correctRequestToChatGPT, any());

        assertEquals("", taskCompletionResponse.getFailureReason());
        assertEquals(true, taskCompletionResponse.getResult());
        verify(taskToUserRepository).save(any());
    }

    @Test
    @DisplayName("Должен вернуть в ответе false, если было передано неверное решение в поле solution")
    void shouldReturnFalseInResponseIfSolutionIsIncorrect() {

        ChatGPTTaskCheckResponse exceptedResponse = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/responsesFromChatGPT/IncorrectResponse.json",
                ChatGPTTaskCheckResponse.class);

        when(chatGptAdapterClient.sendRequestToChatGPT(any())).thenReturn(exceptedResponse);

        TaskCompletionResponse taskCompletionResponse = solutionImpl.getAnswerFromChatGPT(correctRequestToChatGPT, any());

        assertEquals("", taskCompletionResponse.getFailureReason());
        assertEquals(false, taskCompletionResponse.getResult());
        verify(taskToUserRepository, never()).save(any(TaskToUser.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если было передано невалидное решение в поле solution")
    void shouldThrowExceptionIfSolutionIsInvalid() {

        ChatGPTTaskCheckResponse exceptedResponse = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/responsesFromChatGPT/InvalidResponse.json",
                ChatGPTTaskCheckResponse.class);

        when(chatGptAdapterClient.sendRequestToChatGPT(any())).thenReturn(exceptedResponse);

        TaskException exception = assertThrows(TaskException.class, () -> solutionImpl.getAnswerFromChatGPT(correctRequestToChatGPT, any()));
        assertEquals("Отправлены некорректные данные", exception.getMessage());
        verify(taskToUserRepository, never()).save(any(TaskToUser.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение, если было передано невалидное решение в поле solution")
    void shouldThrowExceptionIfSolutionIsInvalid1() {

        ChatGPTTaskCheckResponse exceptedResponse = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/responsesFromChatGPT/FalseResponse.json",
                ChatGPTTaskCheckResponse.class);

        when(chatGptAdapterClient.sendRequestToChatGPT(any())).thenReturn(exceptedResponse);

        TaskException exception = assertThrows(TaskException.class, () -> solutionImpl.getAnswerFromChatGPT(correctRequestToChatGPT, any()));
        assertEquals("Что-то пошло не так, просьба повторить запрос", exception.getMessage());
        verify(taskToUserRepository, never()).save(any(TaskToUser.class));
    }
}