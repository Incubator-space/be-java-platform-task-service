package com.itm.space.taskservice.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class UpdateTaskRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String text;

    private List<@NotNull UUID> attachments;

    @Valid
    private List<TaskSolutionDTO> solutions;
}
