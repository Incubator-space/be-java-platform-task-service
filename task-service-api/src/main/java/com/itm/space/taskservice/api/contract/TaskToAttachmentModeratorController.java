package com.itm.space.taskservice.api.contract;

import com.itm.space.taskservice.api.constant.RoleConstants;
import com.itm.space.taskservice.api.request.TaskToAttachmentModeratorRequest;
import com.itm.space.taskservice.api.response.TaskToAttachmentDeleteResponse;
import com.itm.space.taskservice.api.response.TaskToAttachmentModeratorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

import static com.itm.space.taskservice.api.constant.ApiConstants.MODERATOR_ATTACHMENT_PATH;

@RequestMapping(MODERATOR_ATTACHMENT_PATH)
@Tag(name = "Moderator Аttachment Сontroller", description = "АПИ по добавлению аттачмента к задаче")
public interface TaskToAttachmentModeratorController {

    @Operation(summary = "post", description = "Добавление нового аттачмента к таске")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ на добавление аттачмента к таске"),
            @ApiResponse(responseCode = "400", description = "Неправильные параметры запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
    })
    @PostMapping
    @Secured({RoleConstants.ADMIN, RoleConstants.MODERATOR})
    TaskToAttachmentModeratorResponse addAttachmentToTask(@RequestBody @Valid TaskToAttachmentModeratorRequest request);

    @Operation(summary = "delete", description = "Удаление аттачмента к таске")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ при удалении аттачмента из таски"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Аттачмент для удаления не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
    })
    @DeleteMapping()
    @Secured({RoleConstants.ADMIN, RoleConstants.MODERATOR})
    TaskToAttachmentDeleteResponse deleteAttachmentToTask(@RequestParam(value = "attachmentId") UUID attachmentId);

}