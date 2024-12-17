package com.itm.space.taskservice.controller;

import com.itm.space.taskservice.api.contract.LikeUserController;
import com.itm.space.taskservice.api.response.CommentUserResponse;
import com.itm.space.taskservice.service.CommentToLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LikeControllerImpl implements LikeUserController {

    private final CommentToLikeService commentToLikeService;

    @Override
    public CommentUserResponse addLike(UUID commentId, Principal principal) {
        return commentToLikeService.createLikes(commentId,principal);
    }
}
