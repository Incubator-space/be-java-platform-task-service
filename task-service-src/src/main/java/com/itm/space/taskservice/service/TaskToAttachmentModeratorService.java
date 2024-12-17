package com.itm.space.taskservice.service;

import com.itm.space.taskservice.api.request.TaskToAttachmentModeratorRequest;
import com.itm.space.taskservice.api.response.TaskToAttachmentDeleteResponse;
import com.itm.space.taskservice.api.response.TaskToAttachmentModeratorResponse;

import java.util.UUID;

public interface TaskToAttachmentModeratorService {

    TaskToAttachmentModeratorResponse saveTaskToAttachment(TaskToAttachmentModeratorRequest request);

    TaskToAttachmentDeleteResponse deleteAttachmentToTask(UUID attachmentId);
}