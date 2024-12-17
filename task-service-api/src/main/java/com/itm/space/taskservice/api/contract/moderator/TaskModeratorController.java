package com.itm.space.taskservice.api.contract.moderator;

import com.itm.space.taskservice.api.constant.ApiConstants;
import com.itm.space.taskservice.api.constant.RoleConstants;
import com.itm.space.taskservice.api.request.TaskRequest;
import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.api.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping(ApiConstants.MODERATOR_TASK_BASE_PATH)
@Tag(name = "Task Moderator API", description = "CRUD операции с задачей")
public interface TaskModeratorController {

    @Operation(summary = "createTask", description = "Создание задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача создана"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Тема для создания задачи не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
    })
    @PostMapping
    @Secured({RoleConstants.MODERATOR, RoleConstants.ADMIN})
    TaskResponse createTask(
            @Parameter(description = "Запрос на создание задачи")
            @RequestBody @Valid TaskRequest request
    );

    @Operation(summary = "update", description = "Обновление задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача обновлена"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Задача для обновления не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
    })
    @PutMapping("/{id}")
    @Secured({RoleConstants.MODERATOR, RoleConstants.ADMIN})
    TaskResponse updateTask(
            @Parameter(description = "Запрос на обновление задачи") @RequestBody @Valid UpdateTaskRequest request,
            @PathVariable("id") UUID taskId);
}
