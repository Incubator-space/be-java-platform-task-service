package com.itm.space.taskservice.service.impl;

import com.itm.space.taskservice.api.request.TaskRequest;
import com.itm.space.taskservice.api.request.TaskSolutionDTO;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.SolutionToTask;
import com.itm.space.taskservice.entity.Task;
import com.itm.space.taskservice.entity.TaskToAttachment;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.SolutionToTaskMapper;
import com.itm.space.taskservice.mapper.TaskMapper;
import com.itm.space.taskservice.mapper.TaskToAttachmentMapper;
import com.itm.space.taskservice.mapper.TaskToTopicMapper;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.Task;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.TaskMapper;
import com.itm.space.taskservice.repository.TaskRepository;
import com.itm.space.taskservice.service.SolutionToTaskService;
import com.itm.space.taskservice.service.TaskModeratorService;
import com.itm.space.taskservice.service.TaskToAttachmentService;
import com.itm.space.taskservice.service.TaskToTopicService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskModeratorServiceImpl implements TaskModeratorService {

    private final TaskRepository taskRepository;

    private final TaskToTopicService taskToTopicService;

    private final TaskToAttachmentService taskToAttachmentService;

    private final SolutionToTaskService solutionToTaskService;

    private final TaskMapper taskMapper;

    private final TaskToTopicMapper taskToTopicMapper;

    private final TaskToAttachmentMapper taskToAttachmentMapper;

    private final SolutionToTaskMapper solutionToTaskMapper;

    @Override
    @Transactional
    public TaskResponse createTask(@NonNull TaskRequest request) {

        UUID topicId = request.getTopicId();

        if (!taskToTopicService.existsByTopicId(topicId)) {
            throw new TaskException("Тема для создания задачи не найдена", HttpStatus.NOT_FOUND);
        }

        Task task = taskMapper.taskRequestToTask(request);

        task = taskRepository.save(task);

        UUID taskId = task.getId();

        List<UUID> attachmentIdList = request.getAttachments();
        List<TaskSolutionDTO> taskSolutionDTOList = request.getSolutions();
        List<SolutionToTaskResponse> solutionToTaskResponseList = null;

        saveTaskToTopic(taskId, topicId);

        if (attachmentIdList != null) {
            saveAllTaskToAttachment(attachmentIdList, taskId);
        }

        if (taskSolutionDTOList != null) {
            List<SolutionToTask> solutionToTaskList = saveAllTaskSolutionDTO(taskSolutionDTOList, taskId);

            solutionToTaskResponseList = getSolutionToTaskResponseList(solutionToTaskList);
        }

        return taskMapper.toTaskResponse(task, topicId, attachmentIdList, solutionToTaskResponseList);
    }

    @Override
    public List<SolutionToTask> saveAllTaskSolutionDTO(
            List<TaskSolutionDTO> taskSolutionDTOList, UUID taskId) {

        return solutionToTaskService.saveAll(
                getSolutionToTaskList(taskSolutionDTOList, taskId)
        );
    }

    @Transactional
    @Override
    public void saveAllTaskToAttachment(List<UUID> attachmentIdList, UUID taskId) {
        taskToAttachmentService.saveAll(
                getAttachmentList(attachmentIdList, taskId)
        );
    }

    @Transactional
    @Override
    public void saveTaskToTopic(UUID taskId, UUID topicId) {
        taskToTopicService.save(taskToTopicMapper.taskToTopic(taskId, topicId));
    }

    private List<SolutionToTask> getSolutionToTaskList(
            List<TaskSolutionDTO> taskSolutionDTOList, UUID taskId) {
        return taskSolutionDTOList.stream()
                .map(solutionToTaskRequest ->
                        solutionToTaskMapper.toSolutionToTask(taskId, solutionToTaskRequest))
                .toList();
    }

    private List<SolutionToTaskResponse> getSolutionToTaskResponseList(List<SolutionToTask> solutionToTaskList) {
        return solutionToTaskList.stream()
                .map(solutionToTaskMapper::toSolutionToTaskResponse)
                .toList();
    }

    private List<TaskToAttachment> getAttachmentList(List<UUID> attachmentIdList, UUID taskId) {
        return attachmentIdList.stream()
                .map(attachmentId ->
                        taskToAttachmentMapper.taskToAttachment(taskId, attachmentId))
                .toList();
    }

    @Override
    public Task findTaskByTaskId(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException("Задача для обновления не найдена", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(@NonNull UpdateTaskRequest request, @NonNull UUID taskId) {

        Task updatedTask = findTaskByTaskId(taskId);
        taskMapper.updateTaskFromRequest(request, updatedTask);
        taskRepository.save(updatedTask);

        List<UUID> updatedAttachmentListById = taskToAttachmentService.updateAttachmentListById(request, taskId);

        UUID topicId = taskToTopicService.getTopicIdByTaskId(taskId);

        List<SolutionToTaskResponse> solutionToTaskResponseList = solutionToTaskService.findSolutionsByTaskId(taskId);
        if (request.getSolutions() != null) {
            solutionToTaskResponseList =
                    solutionToTaskService.updateSolutionToTask(request, taskId);
        }

        return taskMapper.toTaskResponse(updatedTask, topicId, updatedAttachmentListById, solutionToTaskResponseList);
    }
}