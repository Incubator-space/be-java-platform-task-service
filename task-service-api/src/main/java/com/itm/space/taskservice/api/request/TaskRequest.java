package com.itm.space.taskservice.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class TaskRequest {

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-zA-Zа-яА-Я])(?=\\S)(?!\\d+$)(?!.*^\\s+$)[a-zA-Z0-9а-яА-Я\\s-\\.]{5,}$")
    private String title;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-zA-Zа-яА-Я])(?=\\S)(?!\\d+$)(?!.*^\\s+$)[a-zA-Z0-9а-яА-Я\\s-\\.]{5,}$")
    private String text;

    @NotNull
    private UUID topicId;

    private List<UUID> attachments;

    private List<TaskSolutionDTO> solutions;

}
