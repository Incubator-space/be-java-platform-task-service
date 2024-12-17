package com.itm.space.taskservice.mapper;

import com.itm.space.taskservice.api.request.TaskToAttachmentModeratorRequest;
import com.itm.space.taskservice.api.response.TaskToAttachmentDeleteResponse;
import com.itm.space.taskservice.api.response.TaskToAttachmentModeratorResponse;
import com.itm.space.taskservice.entity.TaskToAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;


@Mapper
public interface TaskToAttachmentModeratorMapper {

    @Mapping(source = "attachmentId", target = "attachmentId")
    TaskToAttachmentModeratorResponse toAttachmentResponse(TaskToAttachmentModeratorRequest request);

    TaskToAttachment toEntity(TaskToAttachmentModeratorRequest request);

    @Mapping(source = "attachmentId", target = "attachmentId")
    TaskToAttachmentDeleteResponse toEntity(UUID attachmentId);
}