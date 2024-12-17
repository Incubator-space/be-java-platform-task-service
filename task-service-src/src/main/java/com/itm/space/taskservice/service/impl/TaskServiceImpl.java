package com.itm.space.taskservice.service.impl;

import com.itm.space.taskservice.api.request.ChatGPTTaskCheckRequest;
import com.itm.space.taskservice.api.request.CompleteTaskRequestByStudent;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskCompletionResponse;
import com.itm.space.taskservice.api.response.TaskResponse;
import com.itm.space.taskservice.entity.Task;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.kafka.producer.TaskProducer;
import com.itm.space.taskservice.mapper.TaskMapper;
import com.itm.space.taskservice.repository.TaskRepository;
import com.itm.space.taskservice.service.SolutionToTaskService;
import com.itm.space.taskservice.service.TaskService;
import com.itm.space.taskservice.service.TaskToAttachmentService;
import com.itm.space.taskservice.service.TaskToTopicService;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final SolutionToTaskService solutionToTaskService;

    private final TaskRepository taskRepository;

    private final TaskToAttachmentService taskToAttachmentService;

    private final TaskToTopicService taskToTopicService;

    private final TaskMapper taskMapper;

    private final TaskProducer taskProducer;

    @Override
    public boolean existsById(UUID taskId) {
        return taskRepository.existsById(taskId);
    }

    @Override
    @Transactional
    public TaskResponse archivedTaskById(UUID id) {

        Task task = findById(id);

        if (task.isArchived()) {
            throw new TaskException("Задача уже заархивирована.", HttpStatus.BAD_REQUEST);
        }
        task.setArchived(true);
        taskRepository.save(task);

        UUID topicId = taskToTopicService.getTopicIdByTaskId(id);

        List<UUID> attachments = taskToAttachmentService.getAttachmentUUIDsByTaskId(id);

        List<SolutionToTaskResponse> solutions = solutionToTaskService.findSolutionsByTaskId(id);

        TaskResponse taskResponse = taskMapper.toTaskResponse(task, topicId, attachments, solutions);

        return taskResponse;
    }

    @Override
    @Transactional
    public Task findById(UUID id) {

        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskException("Задача не найдена.", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskResponseById(UUID id) {

        Task task = findById(id);

        UUID taskTopicId = taskToTopicService.getTopicIdByTaskId(id);

        List<UUID> taskAttachmentId = taskToAttachmentService.getAttachmentUUIDsByTaskId(id);

        List<SolutionToTaskResponse> solution = solutionToTaskService.findSolutionsByTaskId(id);

        return taskMapper.toTaskResponse(task, taskTopicId, taskAttachmentId, solution);
    }

    @Override
    @Transactional
    public TaskCompletionResponse completeTaskByStudent(CompleteTaskRequestByStudent completeTaskRequestByStudent) {

        UUID taskId = completeTaskRequestByStudent.getTaskId();
        String studentSolution = completeTaskRequestByStudent.getSolution();
        List<UUID> studentSolutionIds = completeTaskRequestByStudent.getSolutionIds();

        if (!taskRepository.existsById(taskId)) {
            throw new TaskException("Задача для завершения не найдена", HttpStatus.NOT_FOUND);
        }

        if ((studentSolution != null && !studentSolution.isBlank()) && !CollectionUtils.isEmpty(studentSolutionIds)){
            throw new TaskException("Неправильные аргументы запроса", HttpStatus.BAD_REQUEST);
        }

        List<SolutionToTaskResponse> taskSolutions =
                solutionToTaskService.findSolutionsByTaskId(taskId);

        Task task = taskRepository.findTaskById(taskId);

        TaskCompletionResponse taskCompletionResponse = null;

        if (!CollectionUtils.isEmpty(studentSolutionIds)) {

            taskCompletionResponse = solutionToTaskService.checkSolutionIds(studentSolutionIds, taskSolutions, taskId);
        }
        else if (studentSolution != null && !studentSolution.isBlank()) {

            ChatGPTTaskCheckRequest chatGPTTaskCheckRequest =
                    new ChatGPTTaskCheckRequest(task.getText(), studentSolution);

            taskCompletionResponse = solutionToTaskService.getAnswerFromChatGPT(chatGPTTaskCheckRequest, taskId);
        }
        else {
            throw new TaskException("Неправильные аргументы запроса: Решение не приложено", HttpStatus.BAD_REQUEST);
        }

        if (taskCompletionResponse == null || taskCompletionResponse.getResult() == false) {
            throw new TaskException("Непредвиденная ошибка в процессе проверки", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else {
            taskProducer.sendingMessageToKafka(taskId);
        }
        return taskCompletionResponse;
    }
}