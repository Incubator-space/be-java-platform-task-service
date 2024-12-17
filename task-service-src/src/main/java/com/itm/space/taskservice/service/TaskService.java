package com.itm.space.taskservice.service;

import com.itm.space.taskservice.api.request.CompleteTaskRequestByStudent;
import com.itm.space.taskservice.api.response.TaskCompletionResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.Task;

import java.util.UUID;

public interface TaskService {

    boolean existsById(UUID taskId);

    TaskResponse archivedTaskById(UUID id);

    Task findById(UUID id);

    TaskResponse getTaskResponseById(UUID id);

    TaskCompletionResponse completeTaskByStudent(CompleteTaskRequestByStudent completeTaskRequestByStudent);
}