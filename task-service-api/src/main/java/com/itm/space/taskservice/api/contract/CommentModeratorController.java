package com.itm.space.taskservice.api.contract;

import static com.itm.space.taskservice.api.constant.RoleConstants.MODERATOR;
import static com.itm.space.taskservice.api.constant.ApiConstants.MODERATE_COMMENT_PATH;

import com.itm.space.taskservice.api.request.ModerateCommentRequest;
import com.itm.space.taskservice.api.response.ModerateCommentResponse;
import com.itm.space.taskservice.api.response.CommentUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Tag(name = "Accept Comment Controller", description = "Операции с отметкой комментариев как прошедших модерацию")
@RequestMapping(MODERATE_COMMENT_PATH)
public interface CommentModeratorController {

    @Operation(summary = "Comment Moderator Controller", description = "Операции с комментариями, доступные только модератору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ на запрос отметки комментариев" +
                    "как прошедших модерацию"),
            @ApiResponse(responseCode = "400", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "401", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Не найден один или несколько комментариев для модерации"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @Secured(MODERATOR)
    @PutMapping("/accept")
    ModerateCommentResponse moderateComment(@Valid @RequestBody ModerateCommentRequest request);

    @Operation(summary = "Soft deletion of a comment to a task by a moderator", description = "Мягкое удаление комментария" +
            "к задаче модератором")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тело ответа с измененным признаком deleted на true"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")

    })
    @DeleteMapping()
    @Secured(MODERATOR)
    CommentUserResponse softDeleteComment(@RequestParam(name = "commentId") UUID id);
}
