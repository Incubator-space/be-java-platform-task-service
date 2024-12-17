package com.itm.space.taskservice.mapper;

import com.itm.space.itmplatformcommonmodels.kafka.TaskCommentEvent;
import com.itm.space.itmplatformcommonmodels.kafka.enums.TaskCommentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface CommentToLikesMapper {

    @Mapping(target = "id", source = "commentId")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "interactorId", source = "userId")
    @Mapping(target = "type", source = "type")
    TaskCommentEvent createTaskCommentEvent(UUID commentId, UUID authorId, UUID userId, TaskCommentType type);
}
