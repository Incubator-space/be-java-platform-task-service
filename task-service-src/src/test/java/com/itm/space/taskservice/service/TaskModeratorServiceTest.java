package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.api.request.TaskRequest;
import com.itm.space.taskservice.api.request.TaskSolutionDTO;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.SolutionToTask;
import com.itm.space.taskservice.entity.Task;
import com.itm.space.taskservice.entity.TaskToAttachment;
import com.itm.space.taskservice.entity.TaskToTopic;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.SolutionToTaskMapper;
import com.itm.space.taskservice.mapper.TaskMapper;
import com.itm.space.taskservice.mapper.TaskToAttachmentMapper;
import com.itm.space.taskservice.mapper.TaskToTopicMapper;
import com.itm.space.taskservice.repository.TaskRepository;
import com.itm.space.taskservice.service.impl.TaskModeratorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskModeratorServiceTest extends BaseUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskToTopicService taskToTopicService;

    @Mock
    private TaskToAttachmentService taskToAttachmentService;

    @Mock
    private SolutionToTaskService solutionToTaskService;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskToTopicMapper taskToTopicMapper;

    @Mock
    private TaskToAttachmentMapper taskToAttachmentMapper;

    @Mock
    private SolutionToTaskMapper solutionToTaskMapper;

    @InjectMocks
    private TaskModeratorServiceImpl taskModeratorService;

    private final TaskRequest testTaskRequest = jsonParserUtil.getObjectFromJson(
            "json/controller/taskModeratorControllerTest/CorrectTaskRequest.json",
            TaskRequest.class
    );

    private final TaskRequest testTaskRequestWithAllField = jsonParserUtil.getObjectFromJson(
            "json/controller/taskModeratorControllerTest/CorrectTaskRequestWithAllField.json",
            TaskRequest.class
    );

    private final TaskRequest testTaskRequestWithNonExistTopicId = jsonParserUtil.getObjectFromJson(
            "json/controller/taskModeratorControllerTest/CorrectTaskRequestWithNonExistTopic.json",
            TaskRequest.class
    );

    private final Task testTask = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/CorrectTask.json",
            Task.class
    );

    private final TaskResponse testTaskResponse = jsonParserUtil.getObjectFromJson(
            "json/controller/taskModeratorControllerTest/CorrectTaskResponse.json",
            TaskResponse.class
    );

    private final TaskResponse testTaskResponseWithAllField = jsonParserUtil.getObjectFromJson(
            "json/controller/taskModeratorControllerTest/CorrectTaskResponseWithAllField.json",
            TaskResponse.class
    );

    private final TaskSolutionDTO testTaskSolutionDTO = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/CorrectTaskSolutionDTO.json",
            TaskSolutionDTO.class
    );

    private final SolutionToTaskResponse testSolutionToTaskResponse = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/CorrectSolutionToTaskResponse.json",
            SolutionToTaskResponse.class
    );

    private final TaskToTopic testTaskToTopic = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/CorrectTaskToTopic.json",
            TaskToTopic.class
    );

    private final TaskToAttachment testTaskToAttachment = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/CorrectTaskToAttachment.json",
            TaskToAttachment.class
    );

    private final SolutionToTask testSolutionToTask = jsonParserUtil.getObjectFromJson(
            "json/service/taskService/CorrectSolutionToTask.json",
            SolutionToTask.class
    );

    private final UUID testTopicId = UUID.fromString("97b608a9-302e-4387-a5f4-fd35969cdc21");

    private final UUID testNonExistTopicId = UUID.fromString("97b608a9-0000-0000-0000-fd35969cdc21");

    private final UUID testTaskId = UUID.fromString("21f315ab-3a57-4afc-a929-9e7a92074a6b");

    private final UUID testAttachmentId = UUID.fromString("497f6eca-6276-4993-bfeb-53cbbbba6f08");

    @Test
    @DisplayName("Удачное создание задачи со всеми полями")
    void shouldCreateTaskWithAllField() {

        List<UUID> testAttachmentIdList = List.of(testAttachmentId);
        List<SolutionToTaskResponse> testSolutionToTaskResponseList = List.of(testSolutionToTaskResponse);

        when(taskMapper.taskRequestToTask(testTaskRequestWithAllField)).thenReturn(testTask);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskToTopicService.existsByTopicId(testTopicId)).thenReturn(true);
        when(taskToTopicMapper.taskToTopic(testTaskId, testTopicId)).thenReturn(testTaskToTopic);
        when(taskToAttachmentMapper.taskToAttachment(testTaskId, testAttachmentId))
                .thenReturn(testTaskToAttachment);
        when(solutionToTaskMapper.toSolutionToTask(testTaskId, testTaskSolutionDTO))
                .thenReturn(testSolutionToTask);
        when(solutionToTaskMapper.toSolutionToTaskResponse(testSolutionToTask)).thenReturn(testSolutionToTaskResponse);
        when(solutionToTaskService.saveAll(List.of(testSolutionToTask))).thenReturn(List.of(testSolutionToTask));
        when(taskMapper.toTaskResponse(testTask, testTopicId, testAttachmentIdList, testSolutionToTaskResponseList))
                .thenReturn(testTaskResponseWithAllField);

        TaskResponse expectedResponse = taskModeratorService.createTask(testTaskRequestWithAllField);

        assertEquals(expectedResponse, testTaskResponseWithAllField);

    }

    @Test
    @DisplayName("Удачное создание задачи")
    void shouldCreateTask() {

        when(taskMapper.taskRequestToTask(testTaskRequest)).thenReturn(testTask);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskToTopicService.existsByTopicId(testTopicId)).thenReturn(true);
        when(taskToTopicMapper.taskToTopic(testTaskId, testTopicId)).thenReturn(testTaskToTopic);
        when(taskMapper.toTaskResponse(testTask, testTopicId, null, null))
                .thenReturn(testTaskResponse);

        TaskResponse expectedResponse = taskModeratorService.createTask(testTaskRequest);

        assertEquals(expectedResponse, testTaskResponse);

    }

    @Test
    @DisplayName("Должен выбрасывать 404, так как тема для задачи не найдена")
    void shouldCreateTask404() {

        when(taskToTopicService.existsByTopicId(testNonExistTopicId)).thenReturn(false);

        assertThrows(
                TaskException.class,
                () -> taskModeratorService.createTask(testTaskRequestWithNonExistTopicId)
        );

    }

    @Test
    @DisplayName("Должен сохранять все TaskSolutionDTO")
    void shouldSaveAllTaskSolutionDTO() {

        List<SolutionToTask> testSolutionToTaskList = List.of(testSolutionToTask);
        List<TaskSolutionDTO> taskSolutionDTOList = testTaskRequestWithAllField.getSolutions();

        when(solutionToTaskMapper.toSolutionToTask(testTaskId, testTaskSolutionDTO))
                .thenReturn(testSolutionToTask);

        taskModeratorService.saveAllTaskSolutionDTO(taskSolutionDTOList, testTaskId);

        verify(solutionToTaskService).saveAll(testSolutionToTaskList);

    }

    @Test
    @DisplayName("Должен сохранять тему задачи")
    void shouldSaveTaskToTopic() {

        when(taskToTopicMapper.taskToTopic(testTaskId, testTopicId)).thenReturn(testTaskToTopic);

        taskModeratorService.saveTaskToTopic(testTaskId, testTopicId);

        verify(taskToTopicService).save(testTaskToTopic);

    }

    @Test
    @DisplayName("Должен сохранять все taskToAttachment")
    void shouldSaveAllTaskToAttachment() {

        List<UUID> testAttachmentIdList = List.of(testAttachmentId);

        List<TaskToAttachment> testTaskToAttachmentList = List.of(testTaskToAttachment);

        when(taskToAttachmentMapper.taskToAttachment(testTaskId, testAttachmentId))
                .thenReturn(testTaskToAttachment);

        taskModeratorService.saveAllTaskToAttachment(testAttachmentIdList, testTaskId);

        verify(taskToAttachmentService).saveAll(testTaskToAttachmentList);
    }

        @Test
        @DisplayName("Тест на успешный поиск таски по id")
        void shouldFindTaskById () {
            Task testTask = jsonParserUtil.getObjectFromJson(
                    "json/service/taskService/CorrectTask.json", Task.class
            );
            UUID testTaskId = UUID.fromString("21f315ab-3a57-4afc-a929-9e7a92074a6b");

            when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));

            Task actualTask = taskModeratorService.findTaskByTaskId(testTaskId);
            assertEquals(testTask, actualTask);
        }

        @Test
        @DisplayName("Тест на отсутствие таски в бд")
        public void testTaskNotFound () {
            UUID incorrectTaskId = UUID.randomUUID();

            when(taskRepository.findById(incorrectTaskId)).thenReturn(java.util.Optional.empty());

            assertThrows(TaskException.class, () -> taskModeratorService.findTaskByTaskId(incorrectTaskId));
        }

        @Test
        @DisplayName("Тест на наличие таска для обновления")
        public void testTaskToUpdateNotFound () {
            UUID incorrectTaskId = UUID.randomUUID();
            UpdateTaskRequest testUpdateTaskRequestWithAllField = jsonParserUtil.getObjectFromJson(
                    "json/controller/taskModeratorControllerTest/CorrectUpdateTaskRequestWithAllFields.json",
                    UpdateTaskRequest.class
            );

            when(taskRepository.findById(incorrectTaskId)).thenReturn(java.util.Optional.empty());

            assertThrows(TaskException.class, () -> taskModeratorService
                    .updateTask(testUpdateTaskRequestWithAllField, incorrectTaskId));
        }

        @Test
        @DisplayName("Тест на успешное обновление таски")
        void shouldUpdateTask () {
            Task testTask = jsonParserUtil.getObjectFromJson(
                    "json/service/taskService/CorrectTask.json",
                    Task.class
            );
            TaskResponse testTaskResponse = jsonParserUtil.getObjectFromJson(
                    "json/controller/taskModeratorControllerTest/CorrectTaskResponse.json",
                    TaskResponse.class
            );
            UpdateTaskRequest testUpdateTaskRequestWithAllField = jsonParserUtil.getObjectFromJson(
                    "json/controller/taskModeratorControllerTest/CorrectUpdateTaskRequestWithAllFields.json",
                    UpdateTaskRequest.class
            );
            SolutionToTaskResponse testSolutionToTaskResponse = jsonParserUtil.getObjectFromJson(
                    "json/service/taskService/CorrectSolutionToTaskResponse.json",
                    SolutionToTaskResponse.class
            );
            UUID testTopicId = UUID.fromString("97b608a9-302e-4387-a5f4-fd35969cdc21");
            UUID testTaskId = UUID.fromString("21f315ab-3a57-4afc-a929-9e7a92074a6b");
            List<SolutionToTaskResponse> testSolutionToTaskResponseList = List.of(testSolutionToTaskResponse);
            List<UUID> testAttachmentIdList = taskToAttachmentService.getAttachmentUUIDsByTaskId(testTaskId);

            when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));
            when(taskToAttachmentService.updateAttachmentListById(testUpdateTaskRequestWithAllField, testTaskId))
                    .thenReturn(testAttachmentIdList);
            when(taskToTopicService.getTopicIdByTaskId(testTaskId)).thenReturn(testTopicId);
            when(solutionToTaskService.updateSolutionToTask(testUpdateTaskRequestWithAllField, testTaskId))
                    .thenReturn(testSolutionToTaskResponseList);
            when(taskMapper.toTaskResponse(testTask, testTopicId, testAttachmentIdList, testSolutionToTaskResponseList))
                    .thenReturn(testTaskResponse);

            TaskResponse actualResponse = taskModeratorService.updateTask(testUpdateTaskRequestWithAllField, testTaskId);

            assertEquals(actualResponse, testTaskResponse);
        }
    }
