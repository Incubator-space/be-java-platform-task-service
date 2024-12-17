package com.itm.space.taskservice.service.impl;

import com.itm.space.taskservice.api.request.ChatGPTTaskCheckRequest;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.response.ChatGPTTaskCheckResponse;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskCompletionResponse;
import com.itm.space.taskservice.entity.SolutionToTask;
import com.itm.space.taskservice.entity.TaskToUser;
import com.itm.space.taskservice.exception.TaskException;
import com.itm.space.taskservice.mapper.SolutionToTaskMapper;
import com.itm.space.taskservice.repository.SolutionToTaskRepository;
import com.itm.space.taskservice.repository.TaskToUserRepository;
import com.itm.space.taskservice.service.SolutionToTaskService;
import com.itm.space.taskservice.util.SecurityUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionToTaskServiceImpl implements SolutionToTaskService {

    private final SolutionToTaskRepository solutionToTaskRepository;

    private final SolutionToTaskMapper solutionToTaskMapper;

    private final TaskToUserRepository taskToUserRepository;

    private final ChatGptAdapterClientService chatGptAdapterClient;

    private final SecurityUtil securityUtil;

    private static final String RESULT_CORRECT = "CORRECT";

    private static final String RESULT_INCORRECT = "INCORRECT";

    private static final String RESULT_INVALID = "INVALID";

    @Transactional
    @Override
    public List<SolutionToTask> saveAll(List<SolutionToTask> solutionToTaskList) {
        return solutionToTaskRepository.saveAll(solutionToTaskList);
    }

    @Override
    @Transactional
    public void deleteAllSolutionsByTaskId(UUID taskId) {
        solutionToTaskRepository.deleteAllSolutionsByTaskId(taskId);
    }

    @Override
    @Transactional
    public List<SolutionToTaskResponse> findSolutionsByTaskId(UUID taskId) {

        List<SolutionToTask> solutions = solutionToTaskRepository.findSolutionsByTaskId(taskId);

        List<SolutionToTaskResponse> responses = solutions.stream()
                .map(solutionToTaskMapper::toSolutionToTaskResponse)
                .collect(Collectors.toList());

        return responses;
    }

    @Override
    @Transactional
    public Map<UUID, List<SolutionToTaskResponse>> findSolutionToTaskById(List<UUID> taskIds) {

        return solutionToTaskRepository.findSolutionToTaskById(taskIds)
                .stream()
                .collect(Collectors.groupingBy(
                        SolutionToTask::getTaskId,
                        Collectors.mapping(solutionToTaskMapper::toSolutionToTaskResponse, Collectors.toList())
                ));
    }

    @Override
    @Transactional
    public List<SolutionToTaskResponse> updateSolutionToTask(
            @NonNull UpdateTaskRequest request, @NonNull UUID taskId) {

        solutionToTaskRepository.deleteAllSolutionsByTaskId(taskId);

        List<SolutionToTask> newSolutions = request.getSolutions().stream()
                .map(solutionToTaskDTO -> solutionToTaskMapper.toSolutionToTask(taskId, solutionToTaskDTO))
                .toList();

        List<SolutionToTask> savedSolutions = solutionToTaskRepository.saveAll(newSolutions);

        return savedSolutions.stream()
                .map(solutionToTaskMapper::toSolutionToTaskResponse)
                .toList();
    }

    public TaskCompletionResponse checkSolutionIds(List<UUID> studentSolutionIds,
                                                   List<SolutionToTaskResponse> taskSolutions, UUID taskId) {

        StringBuilder failureReason = new StringBuilder();

        for (UUID selectedSolutionId : studentSolutionIds) {
            boolean solutionFound = false;
            for (SolutionToTaskResponse solution : taskSolutions) {
                if (solution.getId().equals(selectedSolutionId)) {
                    solutionFound = true;
                    if (!solution.isCorrect()) {
                        failureReason.append("Указанное решение с ID ").append(selectedSolutionId).append(" неверно.");
                        return new TaskCompletionResponse(false, failureReason.toString());
                    }
                }
            }
            if (!solutionFound) {
                failureReason.append("Указанное решение с ID ")
                        .append(selectedSolutionId)
                        .append(" не принадлежит задаче с ID ")
                        .append(taskId);
                throw new TaskException(failureReason.toString(), HttpStatus.BAD_REQUEST);
            }
        }

        TaskToUser tasktoUser = new TaskToUser(taskId, securityUtil.getCurrentUserId());
        taskToUserRepository.save(tasktoUser);

        return new TaskCompletionResponse(true, "");
    }

    public TaskCompletionResponse getAnswerFromChatGPT(ChatGPTTaskCheckRequest chatGPTTaskCheckRequest,
                                                       UUID taskId) {

        ChatGPTTaskCheckResponse answerFromChatGPT = chatGptAdapterClient.sendRequestToChatGPT(chatGPTTaskCheckRequest);

        switch (answerFromChatGPT.getResult()) {
            case RESULT_CORRECT -> {

                TaskToUser tasktoUser = new TaskToUser(taskId, securityUtil.getCurrentUserId());
                taskToUserRepository.save(tasktoUser);

                return new TaskCompletionResponse(true, answerFromChatGPT.getReason());
            }
            case RESULT_INCORRECT -> {
                return new TaskCompletionResponse(false, answerFromChatGPT.getReason());
            }
            case RESULT_INVALID -> throw new TaskException("Отправлены некорректные данные", HttpStatus.BAD_REQUEST);

            default ->
                    throw new TaskException("Что-то пошло не так, просьба повторить запрос", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}