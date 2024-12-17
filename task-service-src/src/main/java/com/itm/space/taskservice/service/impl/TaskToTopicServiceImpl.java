package com.itm.space.taskservice.service.impl;

import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.Task;
import com.itm.space.taskservice.entity.TaskToTopic;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.TaskMapper;
import com.itm.space.taskservice.repository.TaskRepository;
import com.itm.space.taskservice.repository.TaskToTopicRepository;
import com.itm.space.taskservice.service.SolutionToTaskService;
import com.itm.space.taskservice.service.TaskToAttachmentService;
import com.itm.space.taskservice.service.TaskToTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TaskToTopicServiceImpl implements TaskToTopicService {

    private final TaskToTopicRepository taskToTopicRepository;

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final TaskToAttachmentService taskToAttachmentService;

    private final SolutionToTaskService solutionToTaskService;

    @Override
    public void save(TaskToTopic taskToTopic) {
        taskToTopicRepository.save(taskToTopic);
    }

    @Override
    public boolean existsByTopicId(UUID topicId) {
        return taskToTopicRepository.existsByTopicId(topicId);
    }

    public UUID getTopicIdByTaskId(UUID taskId) {
        return taskToTopicRepository.findTopicIdByTaskId(taskId)
                .orElseThrow(() -> new TaskException("Тема задачи не найдена", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksToTopicId(UUID topicId) {

        if (!taskToTopicRepository.existsById(topicId)) {
            throw new TaskException("Тема для получения задач не найдена", HttpStatus.NOT_FOUND);
        }

        List<Task> tasks = taskRepository.findAllByTopicIdAndNotArchived(topicId);

        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> taskIds = tasks.stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        Map<UUID, List<UUID>> attachmentsByTaskId = taskToAttachmentService.findTaskToAttachmentsById(taskIds);
        Map<UUID, List<SolutionToTaskResponse>> solutionsByTaskId = solutionToTaskService.findSolutionToTaskById(taskIds);

        return tasks.stream()
                .map(task -> {
                    List<UUID> taskAttachments = attachmentsByTaskId.getOrDefault(task.getId(), Collections.emptyList());
                    List<SolutionToTaskResponse> taskSolutions = solutionsByTaskId.getOrDefault(task.getId(), Collections.emptyList());
                    return taskMapper.toTaskResponse(task, topicId, taskAttachments, taskSolutions);
                })
                .collect(Collectors.toList());
    }
}