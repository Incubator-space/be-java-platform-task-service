package com.itm.space.taskservice.service;

import com.itm.space.taskservice.api.request.ChatGPTTaskCheckRequest;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.response.SolutionToTaskResponse;
import com.itm.space.taskservice.api.response.TaskCompletionResponse;
import com.itm.space.taskservice.entity.SolutionToTask;
import com.itm.space.taskservice.entity.TaskToUser;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SolutionToTaskService {

    List<SolutionToTask> saveAll(List<SolutionToTask> solutionToTaskList);

    List<SolutionToTaskResponse> findSolutionsByTaskId(UUID taskId);

    Map<UUID, List<SolutionToTaskResponse>> findSolutionToTaskById (List<UUID> taskIds);

    void deleteAllSolutionsByTaskId(UUID taskId);

    List<SolutionToTaskResponse> updateSolutionToTask(@NonNull UpdateTaskRequest request, @NonNull UUID taskId);

    TaskCompletionResponse checkSolutionIds(List<UUID> studentSolutionIds,
                                            List<SolutionToTaskResponse> taskSolutions, UUID taskId);

    TaskCompletionResponse getAnswerFromChatGPT(ChatGPTTaskCheckRequest chatGPTTaskCheckRequest,
                                                UUID taskId);
}
