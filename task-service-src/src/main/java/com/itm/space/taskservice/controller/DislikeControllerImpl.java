package com.itm.space.taskservice.controller;

import com.itm.space.taskservice.api.contract.DislikeUserController;
import com.itm.space.taskservice.api.response.CommentUserResponse;
import com.itm.space.taskservice.service.CommentToDislikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DislikeControllerImpl implements DislikeUserController {

    private final CommentToDislikeService commentToDislikeService;

    @Override
    public CommentUserResponse addDislike(UUID commentId, Principal principal) {
        return commentToDislikeService.createDislikes(commentId,principal);
    }
}
