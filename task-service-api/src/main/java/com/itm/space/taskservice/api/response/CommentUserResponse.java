package com.itm.space.taskservice.api.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUserResponse {

    @NotNull
    private UUID id;

    @NotNull
    private String text;

    @NotNull
    private String authorName;

    @NotNull
    private boolean actual;

    @NotNull
    private UUID taskId;

    @NotNull
    private Integer like;

    @NotNull
    private Integer dislike;

    @NotNull
    private boolean moderated;

    @NotNull
    private boolean deleted;

    private UUID parentId;

    @NotNull
    private UUID authorId;
}
