package com.itm.space.taskservice.controller;

import com.itm.space.taskservice.api.contract.moderator.TaskStudentController;
import com.itm.space.taskservice.api.request.CompleteTaskRequestByStudent;
import com.itm.space.taskservice.api.response.TaskCompletionResponse;
import com.itm.space.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.itm.space.taskservice.api.constant.ApiConstants.STUDENT_TASK_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(STUDENT_TASK_PATH)
public class TaskStudentControllerImpl implements TaskStudentController {

    private final TaskService taskService;

    @Override
    @PostMapping("/complete")
    public TaskCompletionResponse completeTask(@RequestBody @Valid CompleteTaskRequestByStudent completeTaskRequestByStudent) {
        return taskService.completeTaskByStudent(completeTaskRequestByStudent);
    }
}
