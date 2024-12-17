package com.itm.space.taskservice.api.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

    @NotNull
    private UUID id;

    @NotNull
    private String title;

    @NotNull
    private String text;

    @NotNull
    private UUID topicId;

    @NotNull
    private List<UUID> attachments;

    @NotNull
    private boolean archived;

    private List<SolutionToTaskResponse> solutions;
}
