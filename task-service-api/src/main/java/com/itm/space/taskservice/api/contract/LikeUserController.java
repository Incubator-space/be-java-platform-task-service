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

@Tag(name = "Like user controller", description = "Лайк комментария другого пользователя. Нельзя поставить лайк самому себе.")
@RequestMapping(ApiConstants.STUDENT_LIKE_COMMENTS)
public interface LikeUserController {

    @Operation(summary = " get like comment", description = "Получение лайка чужого комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Тело ответа на запрос по комментарию"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Комментарий для лайка не найден"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })

    @PatchMapping("/{commentId}/like")
    @Secured(RoleConstants.STUDENT)
    CommentUserResponse addLike(@PathVariable("commentId") UUID commentId, Principal principal);
}
