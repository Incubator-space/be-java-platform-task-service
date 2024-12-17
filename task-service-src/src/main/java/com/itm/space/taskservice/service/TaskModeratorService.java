package com.itm.space.taskservice.service;

import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.Task;
import lombok.NonNull;

import com.itm.space.taskservice.api.request.TaskRequest;
import com.itm.space.taskservice.api.request.TaskSolutionDTO;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.SolutionToTask;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface TaskModeratorService {

    TaskResponse createTask(@NonNull TaskRequest request);

    List<SolutionToTask> saveAllTaskSolutionDTO(List<TaskSolutionDTO> taskSolutionDTOList, UUID taskId);

    void saveAllTaskToAttachment(List<UUID> attachmentIdList, UUID taskId);

    void saveTaskToTopic(UUID taskId, UUID topicId);

    Task findTaskByTaskId(UUID taskId);

    TaskResponse updateTask(@NonNull UpdateTaskRequest request, @NonNull UUID taskId);
}