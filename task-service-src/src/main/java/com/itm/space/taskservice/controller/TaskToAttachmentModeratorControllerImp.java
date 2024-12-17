package com.itm.space.taskservice.controller;

import com.itm.space.taskservice.api.contract.TaskToAttachmentModeratorController;
import com.itm.space.taskservice.api.request.TaskToAttachmentModeratorRequest;
import com.itm.space.taskservice.api.response.TaskToAttachmentDeleteResponse;
import com.itm.space.taskservice.api.response.TaskToAttachmentModeratorResponse;
import com.itm.space.taskservice.service.TaskToAttachmentModeratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TaskToAttachmentModeratorControllerImp implements TaskToAttachmentModeratorController {

    private final TaskToAttachmentModeratorService taskToAttachmentService;

    @Override
    public TaskToAttachmentModeratorResponse addAttachmentToTask(@RequestBody @Valid TaskToAttachmentModeratorRequest request) {
        return taskToAttachmentService.saveTaskToAttachment(request);
    }

    @Override
    public TaskToAttachmentDeleteResponse deleteAttachmentToTask(UUID attachmentId) {
        return taskToAttachmentService.deleteAttachmentToTask(attachmentId);
    }
}