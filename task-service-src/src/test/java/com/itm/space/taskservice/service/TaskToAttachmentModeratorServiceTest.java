package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.api.request.TaskToAttachmentModeratorRequest;
import com.itm.space.taskservice.api.response.TaskToAttachmentModeratorResponse;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.TaskToAttachmentModeratorMapper;
import com.itm.space.taskservice.repository.TaskToAttachmentRepository;
import com.itm.space.taskservice.service.impl.TaskToAttachmentModeratorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskToAttachmentModeratorServiceTest extends BaseUnitTest {

    @Mock
    private TaskToAttachmentRepository taskToAttachmentRepository;

    @Mock
    private TaskToAttachmentService attachmentService;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskToAttachmentModeratorMapper mapper;

    @InjectMocks
    private TaskToAttachmentModeratorServiceImpl service;

    private final TaskToAttachmentModeratorRequest testTaskToAttachmentAddRequest = jsonParserUtil.getObjectFromJson(
            "json/controller/taskToAttachmentModeratorRequest.json",
            TaskToAttachmentModeratorRequest.class
    );

    @Test
    @DisplayName("Тестирование добавления вложения к существующей задаче")
    void testSaveToAttachmentSuccess() {
        TaskToAttachmentModeratorRequest request = testTaskToAttachmentAddRequest;
        TaskToAttachmentModeratorResponse expectedResponse = jsonParserUtil.getObjectFromJson(
                "json/controller/taskToAttachmentModeratorResponse.json", TaskToAttachmentModeratorResponse.class
        );

        when(taskService.existsById(request.getTaskId())).thenReturn(true);
        when(attachmentService.existsByTaskIdAndAttachmentId(
                request.getTaskId(),
                request.getAttachmentId()
        )).thenReturn(false);
        when(mapper.toAttachmentResponse(request)).thenReturn(expectedResponse);

        TaskToAttachmentModeratorResponse actualResponse = service.saveTaskToAttachment(request);

        assertEquals(expectedResponse, actualResponse);
        verify(taskService).existsById(request.getTaskId());
        verify(attachmentService).existsByTaskIdAndAttachmentId(request.getTaskId(), request.getAttachmentId());
        verify(taskToAttachmentRepository).save(mapper.toEntity(request));
        verify(mapper).toAttachmentResponse(request);
    }

    @Test
    @DisplayName("Тестирование добавления вложения к несуществующей задаче")
    void testSaveToAttachmentTaskNotFound() {
        TaskToAttachmentModeratorRequest request = jsonParserUtil.getObjectFromJson(
                "json/controller/taskToAttacmentIdIncorrectRequest.json",
                TaskToAttachmentModeratorRequest.class
        );

        when(taskService.existsById(request.getTaskId())).thenReturn(false);

        assertThrows(TaskException.class, () -> service.saveTaskToAttachment(request));
        verify(taskService).existsById(request.getTaskId());
    }

    @Test
    @DisplayName("Тестирование добавления вложения к существующей задаче, когда вложение уже существует")
    void testSaveToAttachmentAttachmentAlreadyExists() {
        TaskToAttachmentModeratorRequest request = testTaskToAttachmentAddRequest;

        when(taskService.existsById(request.getTaskId())).thenReturn(true);
        when(attachmentService.existsByTaskIdAndAttachmentId(
                request.getTaskId(),
                request.getAttachmentId()
        )).thenReturn(true);

        assertThrows(TaskException.class, () -> service.saveTaskToAttachment(request));
        verify(taskService).existsById(request.getTaskId());
        verify(attachmentService).existsByTaskIdAndAttachmentId(request.getTaskId(), request.getAttachmentId());
    }
}