package com.itm.space.taskservice.service;

import com.itm.space.taskservice.api.response.CommentUserResponse;

import java.security.Principal;
import java.util.UUID;

public interface CommentToDislikeService {

    CommentUserResponse createDislikes(UUID commentId, Principal principal);
}
