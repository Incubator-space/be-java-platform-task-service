package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.entity.TaskToAttachment;
import com.itm.space.taskservice.repository.TaskToAttachmentRepository;
import com.itm.space.taskservice.service.impl.TaskToAttachmentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

class TasksToAttachmentServiceTest extends BaseUnitTest {

    @Mock
    private TaskToAttachmentRepository taskToAttachmentRepository;
    @InjectMocks
    private TaskToAttachmentServiceImpl taskToAttachmentService;

    @Test
    @DisplayName("Должен возвращать Map<UUID, List<UUID>>, если taskId существует")
    void shouldFindAttachmentsByTaskId() {

        UUID taskId1 = UUID.randomUUID();
        UUID taskId2 = UUID.randomUUID();
        List<UUID> taskIds = List.of(taskId1, taskId2);
        TaskToAttachment taskToAttachment1 = new TaskToAttachment(taskId1, UUID.randomUUID());
        TaskToAttachment taskToAttachment2 = new TaskToAttachment(taskId1, UUID.randomUUID());
        TaskToAttachment taskToAttachment3 = new TaskToAttachment(taskId2, UUID.randomUUID());

        when(taskToAttachmentRepository.findTaskToAttachmentsById(taskIds))
                .thenReturn(List.of(taskToAttachment1, taskToAttachment2, taskToAttachment3));

        Map<UUID, List<UUID>> result = taskToAttachmentService.findTaskToAttachmentsById(taskIds);

        assertThat(result).hasSize(2);
        assertThat(result).containsKey(taskId1);
        assertThat(result).containsKey(taskId2);
        assertThat(result.get(taskId1)).containsExactlyInAnyOrder(
                taskToAttachment1.getAttachmentId(), taskToAttachment2.getAttachmentId());
        assertThat(result.get(taskId2)).containsExactly(taskToAttachment3.getAttachmentId());
    }

    @Test
    @DisplayName("Должен возвращать пустой Map, если taskId не существует")
    void shouldReturnEmptyMapWhenTaskIdDoesNotExist() {

        UUID nonExistentTaskId = UUID.randomUUID();
        List<UUID> taskIds = List.of(nonExistentTaskId);

        when(taskToAttachmentRepository.findTaskToAttachmentsById(taskIds)).thenReturn(Collections.emptyList());

        Map<UUID, List<UUID>> result = taskToAttachmentService.findTaskToAttachmentsById(taskIds);

        assertThat(result).isEmpty();
    }
}



