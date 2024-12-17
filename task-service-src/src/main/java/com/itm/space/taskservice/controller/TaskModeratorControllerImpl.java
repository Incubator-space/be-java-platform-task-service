package com.itm.space.taskservice.controller;

import com.itm.space.taskservice.api.contract.moderator.TaskModeratorController;
import com.itm.space.taskservice.api.request.TaskRequest;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.service.impl.TaskModeratorServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskModeratorControllerImpl implements TaskModeratorController {

    private final TaskModeratorServiceImpl taskModeratorService;

    @Override
    public TaskResponse createTask(TaskRequest request) {
        return taskModeratorService.createTask(request);
    }

    public TaskResponse updateTask(@RequestBody @Valid UpdateTaskRequest request,
                                   @PathVariable("id") UUID taskId) {
        return taskModeratorService.updateTask(request, taskId);
    }
}

