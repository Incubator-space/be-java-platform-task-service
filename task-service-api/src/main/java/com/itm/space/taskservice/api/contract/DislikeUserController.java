package com.itm.space.taskservice.api.contract;

import com.itm.space.taskservice.api.constant.ApiConstants;
import com.itm.space.taskservice.api.constant.RoleConstants;
import com.itm.space.taskservice.api.response.CommentUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.UUID;

@Tag(name = "Dislike user controller", description = "Дизлайк комментария другого пользователя. Нельзя поставить дизлайк самому себе.")
@RequestMapping(ApiConstants.STUDENT_DISLIKE_COMMENTS)
public interface DislikeUserController {

    @Operation(summary = " get dislike comment", description = "Получение дизлайка чужого комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тело ответа на запрос по комментарию"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Комментарий для дизлайка не найден"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PatchMapping("/{commentId}/dislike")
    @Secured(RoleConstants.STUDENT_TO_DISLIKES)
    CommentUserResponse addDislike(@PathVariable("commentId") UUID commentId, Principal principal);
}