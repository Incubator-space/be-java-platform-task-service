package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.api.response.TaskToAttachmentDeleteResponse;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.TaskToAttachmentModeratorMapper;
import com.itm.space.taskservice.repository.TaskToAttachmentRepository;
import com.itm.space.taskservice.service.impl.TaskToAttachmentModeratorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskToAttachmentServiceImplTest extends BaseUnitTest {

    @InjectMocks
    private TaskToAttachmentModeratorServiceImpl taskToAttachmentModeratorService;

    @Mock
    private TaskToAttachmentRepository taskToAttachmentRepository;

    @Mock
    private TaskToAttachmentService attachmentService;

    @Mock
    private TaskToAttachmentModeratorMapper mapper;


    @Test
    @DisplayName("Тест удаляет аттачмент из task_to_attachment")
    void deleteToAttachmentFromTaskShouldDeleteAttachmentAndReturnResponse() {

        UUID attachmentId = UUID.randomUUID();
        TaskToAttachmentDeleteResponse expectedResponse = new TaskToAttachmentDeleteResponse(attachmentId);
        doNothing().when(taskToAttachmentRepository).deleteAttachment(attachmentId);
        when(attachmentService.existsById(attachmentId)).thenReturn(true);
        when(mapper.toEntity(attachmentId)).thenReturn(new TaskToAttachmentDeleteResponse(attachmentId));
        TaskToAttachmentDeleteResponse actualResponse = taskToAttachmentModeratorService.deleteAttachmentToTask(attachmentId);
        assertEquals(expectedResponse, actualResponse);
        verify(taskToAttachmentRepository).deleteAttachment(attachmentId);
    }

    @Test
    @DisplayName("Tecт проверяет соответствие ошибки")
    void deleteToAttachmentFromTaskShouldThrowTaskExceptionWhenAttachmentDoesNotExist() {

        UUID attachmentId = UUID.randomUUID();
        when(attachmentService.existsById(attachmentId)).thenReturn(false);
        assertThrows(TaskException.class, () -> taskToAttachmentModeratorService.deleteAttachmentToTask(attachmentId));
    }
}