package com.itm.space.taskservice.api.request;

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
public class TaskToAttachmentModeratorRequest {

    @NotNull
    private UUID taskId;

    @NotNull
    private UUID attachmentId;
}