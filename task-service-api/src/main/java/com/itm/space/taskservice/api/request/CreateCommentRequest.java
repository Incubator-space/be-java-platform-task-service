package com.itm.space.taskservice.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotNull
    private UUID taskId;

    private UUID parentId;

    @NotBlank(message = "Поле не может быть пустым")
    private String text;

    @Size(max = 3, message = "Список не может содержать больше 3х вложений")
    @Valid
    private List<UUID> attachments;
}
