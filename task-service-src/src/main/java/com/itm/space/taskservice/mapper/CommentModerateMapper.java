package com.itm.space.taskservice.mapper;

import com.itm.space.taskservice.api.request.ModerateCommentRequest;
import com.itm.space.taskservice.api.response.ModerateCommentResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommentModerateMapper {

    @Mapping(source = "commentIds", target = "commentIds")
    ModerateCommentResponse toModerateCommentResponse(ModerateCommentRequest requests);
}
