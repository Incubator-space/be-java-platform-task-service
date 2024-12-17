package com.itm.space.taskservice.mapper;

import com.itm.space.itmplatformcommonmodels.kafka.TaskCommentEvent;
import com.itm.space.itmplatformcommonmodels.kafka.enums.TaskCommentType;
import com.itm.space.taskservice.api.request.CreateCommentRequest;
import com.itm.space.taskservice.api.response.CommentUserResponse;
import com.itm.space.taskservice.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface CommentUserMapper {

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "authorName", source = "comment.authorName")
    @Mapping(target = "actual", source = "comment.actual")
    @Mapping(target = "taskId", source = "comment.taskId")
    @Mapping(target = "like", source = "comment.likes")
    @Mapping(target = "dislike", source = "comment.dislikes")
    @Mapping(target = "moderated", source = "comment.moderated")
    @Mapping(target = "deleted", source = "comment.deleted")
    @Mapping(target = "parentId", source = "comment.parentId")
    @Mapping(target = "authorId", source = "comment.createdBy")
    CommentUserResponse toCommentResponse(Comment comment);

    Comment commentRequestToComment(CreateCommentRequest commentRequest, String authorName);

    @Mapping(source = "comment.id", target = "id")
    @Mapping(source = "userId", target = "authorId")
    @Mapping(source = "userId", target = "interactorId")
    @Mapping(source = "comment.parentId", target = "parentId")
    @Mapping(source = "comment.createdBy", target = "parentAuthorId")
    @Mapping(source = "comment.text", target = "text")
    @Mapping(constant = "UPDATED", target = "type")
    TaskCommentEvent mapToTaskCommentEvent(Comment comment, UUID userId);

    @Mapping(source = "comment.id", target = "id")
    @Mapping(source = "commentId", target = "authorId")
    @Mapping(source = "commentId", target = "interactorId")
    @Mapping(source = "comment.parentId", target = "parentId")
    @Mapping(source = "comment.createdBy", target = "parentAuthorId")
    @Mapping(source = "comment.text", target = "text")
    @Mapping(constant = "DELETED", target = "type")
    TaskCommentEvent mapToDeleteCommentEvent(Comment comment, UUID commentId);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "authorId", source = "userId")
    @Mapping(target = "interactorId", source = "interactorId")
    @Mapping(target = "parentId", source = "comment.parentId")
    @Mapping(target = "parentAuthorId", source = "parentAuthorId")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "type", source = "type")
    TaskCommentEvent toTaskCommentEvent(Comment comment, UUID userId, UUID interactorId, UUID parentAuthorId,
                                        TaskCommentType type);
}