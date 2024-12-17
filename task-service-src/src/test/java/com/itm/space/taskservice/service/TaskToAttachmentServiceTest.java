package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.entity.TaskToAttachment;

import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.mapper.TaskToAttachmentMapper;
import com.itm.space.taskservice.repository.TaskToAttachmentRepository;
import com.itm.space.taskservice.service.impl.TaskToAttachmentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TaskToAttachmentServiceTest extends BaseUnitTest {

    @Mock
    private TaskToAttachmentRepository taskToAttachmentRepository;

    @Mock
    private TaskToAttachmentMapper taskToAttachmentMapper;

    @InjectMocks
    private TaskToAttachmentServiceImpl taskToAttachmentImpl;

    private final UUID taskId = UUID.randomUUID();

    private final UUID attachmentId = UUID.randomUUID();

    @Test
    @DisplayName("Возвращает вложения для задачи по ID задачи")
    void getAttachmentUUIDByTaskId() {
        UUID taskId = UUID.randomUUID();
        List<UUID> attachmentIds = Arrays.asList(UUID.randomUUID(),UUID.randomUUID());

        when(taskToAttachmentRepository.findAttachmentUUIDsByTaskId(taskId)).thenReturn(attachmentIds);

        List<UUID> result = taskToAttachmentImpl.getAttachmentUUIDsByTaskId(taskId);

        assertEquals(attachmentIds,result);
    }

    @Test
    @DisplayName("Сохраняет все вложения для задачи")
    void saveAllTaskToAttachment() {

        List<TaskToAttachment> attachments = Collections.singletonList(any(TaskToAttachment.class));

        taskToAttachmentImpl.saveAll(attachments);

        verify(taskToAttachmentRepository).saveAll(attachments);
    }

    @Test
    @DisplayName("Не найдено вложений для задачи")
    void getAttachmentByTaskIdNull() {

        UUID taskId = UUID.randomUUID();

        when(taskToAttachmentRepository.findAttachmentUUIDsByTaskId(taskId)).thenReturn(Collections.emptyList());

        List<UUID> attachment = taskToAttachmentImpl.getAttachmentUUIDsByTaskId(taskId);
        assertTrue(attachment.isEmpty());
    }

    @Test
    @DisplayName("Тест вернет true, при наличии задачи и вложения")
    void existsByTaskIdAndAttachmentId_ReturnsTrue_WhenTaskAndAttachmentExist() {
        when(taskToAttachmentRepository.existsByTaskIdAndAttachmentId(taskId, attachmentId)).thenReturn(true);

        boolean result = taskToAttachmentImpl.existsByTaskIdAndAttachmentId(taskId, attachmentId);

        assertTrue(result);
    }

    @Test
    @DisplayName("Успешное обновление вложений для задачи")
    void updateAttachmentListById() {
        UUID taskId = UUID.fromString("21f315ab-3a57-4afc-a929-9e7a92074a6b");
        UpdateTaskRequest testUpdateTaskRequestWithAllField = jsonParserUtil.getObjectFromJson(
                "json/controller/taskModeratorControllerTest/CorrectUpdateTaskRequestWithAllFields.json",
                UpdateTaskRequest.class
        );

        when(taskToAttachmentRepository.findAttachmentUUIDsByTaskId(taskId))
                .thenReturn(testUpdateTaskRequestWithAllField.getAttachments());

        List<UUID> attachment = taskToAttachmentImpl.updateAttachmentListById(testUpdateTaskRequestWithAllField, taskId);

        assertEquals(testUpdateTaskRequestWithAllField.getAttachments(), attachment);
    }

    @Test
    @DisplayName("Тест вернет false, если у задачи не существует переданного вложения")
    void existsByTaskIdAndAttachmentId_ReturnsFalse_WhenTaskOrAttachmentDoesNotExist() {
        when(taskToAttachmentRepository.existsByTaskIdAndAttachmentId(taskId, attachmentId)).thenReturn(false);

        boolean result = taskToAttachmentImpl.existsByTaskIdAndAttachmentId(taskId, attachmentId);

        assertFalse(result);
    }
}