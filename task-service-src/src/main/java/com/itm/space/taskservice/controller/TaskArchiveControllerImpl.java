package com.itm.space.taskservice.controller;

import com.itm.space.taskservice.api.contract.TaskArchiveController;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.itm.space.taskservice.api.constant.ApiConstants.TASK_ARCHIVED_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(TASK_ARCHIVED_PATH)
public class TaskArchiveControllerImpl implements TaskArchiveController {

    private final TaskService taskService;

    @DeleteMapping("/{id}")
    public TaskResponse archiveTask(@PathVariable("id") UUID id) {

        return taskService.archivedTaskById(id);
    }
}

