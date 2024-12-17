package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.Task;
import com.itm.space.taskservice.entity.TaskToTopic;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.TaskMapper;
import com.itm.space.taskservice.repository.TaskRepository;
import com.itm.space.taskservice.repository.TaskToTopicRepository;
import com.itm.space.taskservice.service.impl.SolutionToTaskServiceImpl;
import com.itm.space.taskservice.service.impl.TaskToTopicServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskToTopicServiceTest extends BaseUnitTest {

    @InjectMocks
    private TaskToTopicServiceImpl taskToTopicImpl;

    @Mock
    private TaskToTopicRepository taskToTopicRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskToAttachmentService taskToAttachmentService;

    @Mock
    private SolutionToTaskServiceImpl solutionToTaskService;

    @Test
    @DisplayName("Возвращает тему задачи по ID задачи")
    void getTopicIdByTaskId() {

        UUID taskId = UUID.randomUUID();
        UUID topicId = UUID.randomUUID();

        when(taskToTopicRepository.findTopicIdByTaskId(taskId)).thenReturn(Optional.of(topicId));

        UUID result = taskToTopicImpl.getTopicIdByTaskId(taskId);

        assertEquals(topicId, result);
    }

    @Test
    @DisplayName("Возвращает ошибку, если тема задачи не найдена")
    void getTopicIdByTaskIdNull() {

        UUID taskId = UUID.randomUUID();

        when(taskToTopicRepository.findTopicIdByTaskId(taskId)).thenReturn(Optional.empty());

        TaskException exception = assertThrows(TaskException.class, () -> taskToTopicImpl.getTopicIdByTaskId(taskId));

        assertEquals("Тема задачи не найдена", exception.getMessage());
    }

    @Test
    @DisplayName("Должен возвращать true, если тема существует")
    void shouldExistsByTopicIdTrue(){

        UUID topicId = UUID.randomUUID();

        when(taskToTopicRepository.existsByTopicId(topicId)).thenReturn(true);

        assertTrue(taskToTopicImpl.existsByTopicId(topicId));
    }

    @Test
    @DisplayName("Должен возвращать false, если тема отсутствует")
    void shouldExistsByTopicIdFalse(){

        UUID topicId = UUID.randomUUID();

        when(taskToTopicRepository.existsByTopicId(topicId)).thenReturn(false);

        assertFalse(taskToTopicImpl.existsByTopicId(topicId));
    }

    @Test
    @DisplayName("сохраняет тему задачи")
    void saveTaskToTopic() {

        TaskToTopic taskToTopic = any(TaskToTopic.class);

        taskToTopicImpl.save(taskToTopic);

        verify(taskToTopicRepository).save(taskToTopic);
    }

    @Test
    @DisplayName("Должен возвращать Map<UUID, List<SolutionToTaskResponse>>, если тема для получения задач найден")
    void shouldGetListTasksByTopicId() {

        UUID topicId = UUID.randomUUID();
        UUID taskId1 = UUID.randomUUID();
        UUID taskId2 = UUID.randomUUID();
        List<UUID> listTaskId = List.of(taskId1, taskId2);

        Map<UUID, List<UUID>> attachmentsByTaskId = new HashMap<>();
        attachmentsByTaskId.put(taskId1,  List.of(UUID.randomUUID(), UUID.randomUUID()));
        attachmentsByTaskId.put(taskId2,  List.of(UUID.randomUUID()));
        List<UUID> retrievedAttachments1 = attachmentsByTaskId.get(taskId1);
        List<UUID> retrievedAttachments2 = attachmentsByTaskId.get(taskId2);

        Map<UUID, List<SolutionToTaskResponse>> solutionsByTaskId = new HashMap<>();
        solutionsByTaskId.put(taskId1,  List.of(new SolutionToTaskResponse(taskId1, topicId, "text", true)));
        solutionsByTaskId.put(taskId2,  List.of(new SolutionToTaskResponse(taskId2, topicId, "text", true)));
        List<SolutionToTaskResponse> retrievedSolutions1 = solutionsByTaskId.get(taskId1);
        List<SolutionToTaskResponse> retrievedSolutions2 = solutionsByTaskId.get(taskId2);

        Task task1 = new Task("title", "text", false);
        task1.setId(taskId1);
        Task task2 = new Task("title", "text", false);
        task2.setId(taskId2);

        when(taskToTopicRepository.existsById(topicId)).thenReturn(true);
        when(taskRepository.findAllByTopicIdAndNotArchived(topicId)).thenReturn(List.of(task1, task2));
        when(taskToAttachmentService.findTaskToAttachmentsById(listTaskId)).thenReturn(attachmentsByTaskId);
        when(solutionToTaskService.findSolutionToTaskById(listTaskId)).thenReturn(solutionsByTaskId);

        TaskResponse taskResponse1 = new TaskResponse();
        taskResponse1.setAttachments(retrievedAttachments1);
        taskResponse1.setSolutions(retrievedSolutions1);

        TaskResponse taskResponse2 = new TaskResponse();
        taskResponse2.setAttachments(retrievedAttachments2);
        taskResponse2.setSolutions(retrievedSolutions2);

        when(taskMapper.toTaskResponse(task1, topicId, retrievedAttachments1, retrievedSolutions1)).thenReturn(taskResponse1);

        List<TaskResponse> result = taskToTopicImpl.getTasksToTopicId(topicId);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(2, result.get(0).getAttachments().size());
        Assertions.assertEquals(1, result.get(0).getSolutions().size());
    }

    @Test
    @DisplayName("Должен возвращать Exception, если тема для получения задач не найдена")
    void shouldGetExceptionIfTopicNotFound() {

        UUID nonExistentTopicId = UUID.randomUUID();

        when(taskToTopicRepository.existsById(nonExistentTopicId)).thenReturn(false);

        TaskException exception = assertThrows(TaskException.class, () -> taskToTopicImpl.getTasksToTopicId(nonExistentTopicId));

        Assertions.assertEquals("Тема для получения задач не найдена", exception.getMessage());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Должен возвращать Exception, если topicId не валидный")
    void shouldGetExceptionIfTopicInvalid() {

        String invalidTopicId = "InvalidUuidFormat";

        assertThrows(IllegalArgumentException.class, () -> {
            UUID topicId = UUID.fromString(invalidTopicId);
            taskToTopicImpl.getTasksToTopicId(topicId);
        });
    }
}