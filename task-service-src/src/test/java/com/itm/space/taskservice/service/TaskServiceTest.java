package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.api.request.CompleteTaskRequestByStudent;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskCompletionResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.Task;
import com.itm.space.taskservice.entity.TaskToAttachment;
import com.itm.space.taskservice.entity.TaskToTopic;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.kafka.producer.TaskProducer;
import com.itm.space.taskservice.mapper.TaskMapper;
import com.itm.space.taskservice.repository.TaskRepository;
import com.itm.space.taskservice.service.impl.SolutionToTaskServiceImpl;
import com.itm.space.taskservice.service.impl.TaskServiceImpl;
import com.itm.space.taskservice.service.impl.TaskToAttachmentServiceImpl;
import com.itm.space.taskservice.service.impl.TaskToTopicServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class TaskServiceTest extends BaseUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskToTopicServiceImpl taskToTopicImpl;

    @Mock
    private TaskToAttachmentServiceImpl taskToAttachmentImpl;

    @Mock
    private SolutionToTaskServiceImpl solutionImpl;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskProducer taskProducer;

    private final UUID testTaskId = UUID.randomUUID();

    CompleteTaskRequestByStudent completeTaskCorrectRequestWithSolution = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/taskCompleteRequests/CorrectRequestWithSolution.json",
            CompleteTaskRequestByStudent.class);

    private final Task task = jsonParserUtil.getObjectFromJson("json/TaskService/Task.json", Task.class);

    @Test
    @DisplayName("Должен возвращать true, если задача существует")
    void shouldExistsByIdTrue() {

        when(taskRepository.existsById(testTaskId)).thenReturn(true);

        assertTrue(taskService.existsById(testTaskId));
    }

    @Test
    @DisplayName("Должен возвращать false, если задача отсутствует")
    void shouldExistsByIdFalse() {

        when(taskRepository.existsById(testTaskId)).thenReturn(false);

        assertFalse(taskService.existsById(testTaskId));
    }

    @Test
    @DisplayName("Задача успешно архивируется")
    void archivedTask() {

        Task testTask = jsonParserUtil.getObjectFromJson(
                "json/TaskService/TrueTask.json", Task.class);
        TaskToTopic testTaskToTopic = jsonParserUtil.getObjectFromJson(
                "json/TaskService/TrueTaskToTopic.json", TaskToTopic.class);
        TaskToAttachment testTaskToAttachment = jsonParserUtil.getObjectFromJson(
                "json/TaskService/TrueTaskToAttachment.json", TaskToAttachment.class);
        SolutionToTaskResponse expectedSolutionResponse = jsonParserUtil.getObjectFromJson(
                "json/TaskService/ResponseSolutions.json", SolutionToTaskResponse.class);
        TaskResponse expectedResponse = jsonParserUtil.getObjectFromJson(
                "json/TaskService/ResponseTask.json", TaskResponse.class);

        UUID taskId = UUID.fromString("497f6eca-6276-4993-bfeb-53cbbbba6f08");
        UUID topicId = testTaskToTopic.getTopicId();
        List<UUID> attachmentUUIDs = Collections.singletonList(testTaskToAttachment.getAttachmentId());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskToTopicImpl.getTopicIdByTaskId(taskId)).thenReturn(topicId);
        when(taskToAttachmentImpl.getAttachmentUUIDsByTaskId(taskId)).thenReturn(attachmentUUIDs);
        when(solutionImpl.findSolutionsByTaskId(taskId)).thenReturn(List.of(expectedSolutionResponse));
        when(taskMapper.toTaskResponse(testTask, topicId, attachmentUUIDs, List.of(expectedSolutionResponse))).thenReturn(expectedResponse);

        TaskResponse result = taskService.archivedTaskById(taskId);

        assertTrue("Задача должна быть архивирована", result.isArchived());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Невозможно архивировать уже архивированную задачу")
    void archivedTaskBadRequest() {

        Task testTask = jsonParserUtil.getObjectFromJson("json/TaskService/TrueTask.json", Task.class);
        testTask.setArchived(true);

        UUID taskId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        TaskException exception = assertThrows(TaskException.class, () -> taskService.archivedTaskById(taskId));
        assertEquals("Задача уже заархивирована.", exception.getMessage());
    }

    @Test
    @DisplayName("Выбрасывает исключение при отсутствии задачи")
    void taskNotFound() {

        UUID taskId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskException exception = assertThrows(TaskException.class, () -> taskService.archivedTaskById(taskId));
        assertEquals("Задача не найдена.", exception.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть TaskResponse, если задача найдена")
    void shouldTaskResponseById() {

        Task task = jsonParserUtil.getObjectFromJson(
                "json/TaskService/Task.json", Task.class);

        UUID taskId = UUID.fromString("497f6eca-6276-4993-bfeb-53cbbbba6f08");

        UUID topicId = UUID.fromString("97b608a9-302e-4387-a5f4-fd35969cdc21");

        UUID taskToAttachmentId = UUID.fromString("497f6eca-6276-4993-bfeb-53cbbbba6f08");

        SolutionToTaskResponse solutionToTaskResponse = jsonParserUtil.getObjectFromJson(
                "json/TaskService/TaskToSolution.json", SolutionToTaskResponse.class);

        TaskResponse taskResponse = jsonParserUtil.getObjectFromJson(
                "json/TaskService/TaskResponse.json", TaskResponse.class);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskToTopicImpl.getTopicIdByTaskId(taskId)).thenReturn(topicId);
        when(taskToAttachmentImpl.getAttachmentUUIDsByTaskId(taskId)).thenReturn(List.of(taskToAttachmentId));
        when(solutionImpl.findSolutionsByTaskId(taskId)).thenReturn(List.of(solutionToTaskResponse));
        when(taskMapper.toTaskResponse(any(Task.class), any(UUID.class), anyList(), anyList())).thenReturn(taskResponse);

        TaskResponse result = taskService.getTaskResponseById(taskId);

        assertEquals(taskResponse, result);
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(topicId, result.getTopicId());

        verify(taskRepository).findById(taskId);
        verify(taskToTopicImpl).getTopicIdByTaskId(taskId);
        verify(taskToAttachmentImpl).getAttachmentUUIDsByTaskId(taskId);
        verify(solutionImpl).findSolutionsByTaskId(taskId);
        verify(taskMapper).toTaskResponse(any(Task.class), any(UUID.class), anyList(), anyList());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если задача не найдена")
    void shouldTaskResponseNotFound() {

        UUID taskId = UUID.fromString("497f6eca-6276-4993-bfeb-53cbbbba6f08");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskException exception = assertThrows(TaskException.class, () -> taskService.getTaskResponseById(taskId));
        assertEquals("Задача не найдена.", exception.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть в ответе true, если при завершении задачи было передано верное решение в поле solution")
    void shouldReturnTrueInResponseIfSolutionIsCorrect() {

        TaskCompletionResponse exceptedResponse = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/CorrectTaskCompletionResponse.json",
                TaskCompletionResponse.class);

        when(taskRepository.existsById(any())).thenReturn(true);
        when(taskRepository.findTaskById(any())).thenReturn(task);
        when(solutionImpl.getAnswerFromChatGPT(any(),any())).thenReturn(exceptedResponse);

        TaskCompletionResponse taskCompletionResponse = taskService.completeTaskByStudent(completeTaskCorrectRequestWithSolution);

        assertEquals("", taskCompletionResponse.getFailureReason());
        assertEquals(true, taskCompletionResponse.getResult());
    }

    @Test
    @DisplayName("Должен вернуть в ответе true, если при завершении задачи было передано solutionId, у которого " +
                 "результат - true")
    void shouldReturnTrueInResponseIfSolutionIdHasTrueResult() {

        CompleteTaskRequestByStudent completeTaskRequestWithSolutionId = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithSolutionIds.json",
                CompleteTaskRequestByStudent.class);

        TaskCompletionResponse exceptedResponse = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/CorrectTaskCompletionResponse.json",
                TaskCompletionResponse.class);

        List<SolutionToTaskResponse> taskSolutions  = jsonParserUtil.getListFromJson(
                "json/service/taskService/taskCompleteRequests/CorrectListOfSolutionToTaskResponses.json",
                SolutionToTaskResponse.class);

        when(taskRepository.existsById(any())).thenReturn(true);
        when(solutionImpl.findSolutionsByTaskId(any())).thenReturn(taskSolutions);
        when(solutionImpl.checkSolutionIds(any(),any(), any())).thenReturn(exceptedResponse);

        TaskCompletionResponse taskCompletionResponse = taskService.completeTaskByStudent(completeTaskRequestWithSolutionId);

        assertEquals("", taskCompletionResponse.getFailureReason());
        assertEquals(true, taskCompletionResponse.getResult());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если при завершении задачи отсутствует решение")
    void shouldThrowExceptionIfSolutionAndSolutionIdsDoesntExistWhenCompleteTask() {

        CompleteTaskRequestByStudent completeTaskRequestWithoutSolutions = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithoutSolutions.json",
                CompleteTaskRequestByStudent.class);

        when(taskService.existsById(any())).thenReturn(true);

        TaskException exception = assertThrows(TaskException.class, () -> taskService.completeTaskByStudent(completeTaskRequestWithoutSolutions));
        assertEquals("Неправильные аргументы запроса: Решение не приложено", exception.getMessage());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если при завершении задачи присутствуют и решение и айди решений")
    void shouldThrowExceptionIfBothOfSolutionAndSolutionIdsArePresentWhenCompleteTask() {

        CompleteTaskRequestByStudent completeTaskRequestByStudent = jsonParserUtil.getObjectFromJson(
                "json/service/taskService/taskCompleteRequests/IncorrectRequestWithSolutionAndSolutionIds.json",
                CompleteTaskRequestByStudent.class);

        when(taskService.existsById(completeTaskRequestByStudent.getTaskId())).thenReturn(true);

        TaskException exception = assertThrows(TaskException.class, () -> taskService.completeTaskByStudent(completeTaskRequestByStudent));
        assertEquals("Неправильные аргументы запроса", exception.getMessage());
    }

    @Test
    @DisplayName("Должен выбросить исключение, если задача не найдена при завершении задачи")
    void shouldTaskResponseNotFoundWhenCompleteTask() {

        when(taskService.existsById(any())).thenReturn(false);

        TaskException exception = assertThrows(TaskException.class, () -> taskService.completeTaskByStudent(completeTaskCorrectRequestWithSolution));
        assertEquals("Задача для завершения не найдена", exception.getMessage());
    }
}