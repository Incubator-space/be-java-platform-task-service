package com.itm.space.taskservice.api.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCompletionResponse {

    @NotNull
    private Boolean result;

    @NotNull
    private String failureReason;
}
