package com.itm.space.taskservice.api.request;

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
public class CompleteTaskRequestByStudent {

    @NotNull(message = "Task Id should not be null")
    private UUID taskId;

    private String solution;

    private List<UUID> solutionIds;
}
