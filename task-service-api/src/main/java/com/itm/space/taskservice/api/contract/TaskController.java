package com.itm.space.taskservice.api.contract;

import com.itm.space.taskservice.api.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Task Controller", description = "Операции с получением задач")
public interface TaskController {

    @Operation(summary = "getTaskById", description = "Получение задачи по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Тема для получения задачи не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    TaskResponse getTaskById(UUID id);

    @Operation(summary = "Get Tasks To Topic", description = "Получение списка задач по Id топика")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач по топику"),
            @ApiResponse(responseCode = "400", description = "Неправильные параметры запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Тема для получения задач не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    List<TaskResponse> getTasksToTopic(@RequestParam(value = "topicId", required = true) UUID topicId);
}
