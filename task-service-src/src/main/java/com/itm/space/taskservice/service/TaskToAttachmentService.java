package com.itm.space.taskservice.service;

import com.itm.space.taskservice.api.request.UpdateTaskRequest;
import com.itm.space.taskservice.entity.TaskToAttachment;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TaskToAttachmentService {

    void saveAll(List<TaskToAttachment> attachmentList);

    List<UUID> getAttachmentUUIDsByTaskId(UUID taskId);

    boolean existsByTaskIdAndAttachmentId(UUID taskId, UUID attachmentId);

    Map<UUID, List<UUID>> findTaskToAttachmentsById(List<UUID> taskIds);

    void deleteAllAttachmentsByTaskId(UUID taskId);

    List<UUID> updateAttachmentListById(@NonNull UpdateTaskRequest request, @NonNull UUID taskId);

    boolean existsById(UUID attachmentId);
}