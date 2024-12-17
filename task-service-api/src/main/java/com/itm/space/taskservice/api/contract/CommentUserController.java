package com.itm.space.taskservice.api.contract;

import com.itm.space.taskservice.api.request.CreateCommentRequest;
import com.itm.space.taskservice.api.request.UpdateCommentRequest;
import com.itm.space.taskservice.api.response.CommentUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Comment User Controller", description = "Операции с комментариями к задачам пользователя")
public interface CommentUserController {

    @Operation(summary = "Get moderated comments to task", description = "Получение страницы комментариев к задаче, прошедших модерацию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тело ответа на получение страницы комментариев к задаче"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Задача, к которой запрошены комментарии, не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping()
    List<CommentUserResponse> getCommentList(
            @RequestParam(name = "taskId", required = true) UUID taskId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size);

    @Operation(summary = "Creating a comment on the task", description = "Создание комментария к задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тело ответа на запрос по комментарию"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Задача для добавления комментария не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping()
    CommentUserResponse createComment(@RequestBody @Valid CreateCommentRequest userRequest, Principal principal);

    @Operation(summary = "Get moderated comments to task", description = "Получение страницы комментариев к задаче, прошедших модерацию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тело ответа на обновление комментария"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping()
    CommentUserResponse updateComment(@RequestBody UpdateCommentRequest comment, Principal principal);

    @Operation(summary = "Deleting comment", description = "Мягкое удаление комментария к задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тело ответа с измененным признаком deleted на true"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа"),
            @ApiResponse(responseCode = "404", description = "Комментарий для удаления не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping
    CommentUserResponse deleteComment(@RequestParam (name = "commentId") UUID id, Principal principal);
}