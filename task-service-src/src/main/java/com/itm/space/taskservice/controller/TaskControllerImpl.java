package com.itm.space.taskservice.controller;

import com.itm.space.taskservice.api.constant.ApiConstants;
import com.itm.space.taskservice.api.contract.TaskController;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.service.impl.TaskServiceImpl;
import com.itm.space.taskservice.service.impl.TaskToTopicServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.TASK_PATH)
public class TaskControllerImpl implements TaskController {

    private final TaskServiceImpl taskService;

    private final TaskToTopicServiceImpl taskToTopicService;

    @Override
    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable("id") UUID id) {

        return taskService.getTaskResponseById(id);
    }

    @Override
    @GetMapping
    public List<TaskResponse> getTasksToTopic(@RequestParam(value = "topicId", required = false) UUID topicId) {
        if (topicId == null) {
            throw new IllegalArgumentException("Укажите идентификатор темы.");
        }
        return taskToTopicService.getTasksToTopicId(topicId);
    }
}
