package com.itm.space.taskservice.mapper;

import com.itm.space.taskservice.entity.TaskToAttachment;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public interface TaskToAttachmentMapper {

    TaskToAttachment taskToAttachment(UUID taskId, UUID attachmentId);
}