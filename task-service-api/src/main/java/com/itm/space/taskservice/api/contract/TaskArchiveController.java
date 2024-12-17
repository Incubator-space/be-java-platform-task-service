package com.itm.space.taskservice.api.contract;

import com.itm.space.taskservice.api.constant.RoleConstants;
import com.itm.space.taskservice.api.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static com.itm.space.taskservice.api.constant.ApiConstants.TASK_ARCHIVED_PATH;

@Tag(name = "Task Archive Controller", description = "Операции с архивацией задач")
@RequestMapping(TASK_ARCHIVED_PATH)
public interface TaskArchiveController {
    @Operation(summary = "Archive task", description = "Архивация задачи по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ на запрос архивации задачи"),
            @ApiResponse(responseCode = "400", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "401", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Не найдена задача для архивации"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
    })
    @Secured({RoleConstants.MODERATOR, RoleConstants.ADMIN})
    @DeleteMapping("/{id}")
    TaskResponse archiveTask(@Parameter(description = "Запрос на архивирование задачи")
                             @PathVariable("id") UUID id);
}