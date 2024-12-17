package com.itm.space.taskservice.api.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolutionToTaskResponse {

    @NotNull
    private UUID id;

    @NotNull
    private UUID taskId;

    @NotNull
    private String text;

    @NotNull
    private boolean correct;
}