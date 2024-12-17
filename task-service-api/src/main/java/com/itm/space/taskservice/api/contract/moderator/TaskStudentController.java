package com.itm.space.taskservice.api.contract.moderator;

import com.itm.space.taskservice.api.constant.RoleConstants;
import com.itm.space.taskservice.api.request.CompleteTaskRequestByStudent;
import com.itm.space.taskservice.api.response.TaskCompletionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.itm.space.taskservice.api.constant.ApiConstants.STUDENT_TASK_PATH;

@Tag(name = "Student Task Controller", description = "Операции с задачами студента")
@RequestMapping(STUDENT_TASK_PATH)
public interface TaskStudentController {

    @Operation(summary = "Complete task", description = "Завершение задачи студентом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ на завершение задачи"),
            @ApiResponse(responseCode = "400", description = "Неправильные аргументы запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "404", description = "Задача для завершения не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
    })
    @Secured({RoleConstants.STUDENT})
    @PostMapping("/complete")
    TaskCompletionResponse completeTask(CompleteTaskRequestByStudent completeTaskRequestByStudent);
}
