package com.itm.space.taskservice.service;

import com.itm.space.taskservice.BaseUnitTest;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.entity.SolutionToTask;
import com.itm.space.taskservice.mapper.SolutionToTaskMapper;
import com.itm.space.taskservice.repository.SolutionToTaskRepository;
import com.itm.space.taskservice.service.impl.SolutionToTaskServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

class SolutionsToTaskServiceTest extends BaseUnitTest {

    @Mock
    private SolutionToTaskRepository solutionToTaskRepository;
    @Mock
    private SolutionToTaskMapper solutionToTaskMapper;
    @InjectMocks
    private SolutionToTaskServiceImpl solutionToTaskService;

    @Test
    @DisplayName("Должен возвращать  Map<UUID, List<SolutionToTaskResponse>>, если taskId существует")
    void shouldGetSolutionsByTaskId() {

        UUID taskId1 = UUID.randomUUID();
        UUID taskId2 = UUID.randomUUID();
        List<UUID> taskIds = List.of(taskId1, taskId2);

        SolutionToTask solutionToTask1 = new SolutionToTask(taskId1, "text", true);
        SolutionToTask solutionToTask2 = new SolutionToTask(taskId2, "text", false);
        SolutionToTaskResponse response1 = new SolutionToTaskResponse(UUID.randomUUID(), taskId1, "text", true);
        SolutionToTaskResponse response2 = new SolutionToTaskResponse(UUID.randomUUID(), taskId2, "text", false);

        when(solutionToTaskRepository.findSolutionToTaskById(taskIds)).thenReturn(List.of(solutionToTask1, solutionToTask2));
        when(solutionToTaskMapper.toSolutionToTaskResponse(solutionToTask1)).thenReturn(response1);
        when(solutionToTaskMapper.toSolutionToTaskResponse(solutionToTask2)).thenReturn(response2);

        Map<UUID, List<SolutionToTaskResponse>> result = solutionToTaskService.findSolutionToTaskById(taskIds);

        assertThat(result).hasSize(2);
        assertThat(response1.getId()).isNotNull();
        assertThat(response1.getTaskId()).isEqualTo(taskId1);
        assertThat(response2.getId()).isNotNull();
        assertThat(response2.getTaskId()).isEqualTo(taskId2);
        assertThat(result).doesNotContainKey(UUID.randomUUID());
    }

    @Test
    @DisplayName("Должен возвращать пустой Map<UUID, List<SolutionToTaskResponse>>, если taskIds не существует")
    void shouldGetSolutionsByTaskIdWhenTaskIdNotFound() {

        UUID nonExistentTaskId = UUID.randomUUID();
        List<UUID> taskIds = List.of(nonExistentTaskId);

        Map<UUID, List<SolutionToTaskResponse>> result = solutionToTaskService.findSolutionToTaskById(taskIds);

        assertThat(result).isEmpty();
    }
}