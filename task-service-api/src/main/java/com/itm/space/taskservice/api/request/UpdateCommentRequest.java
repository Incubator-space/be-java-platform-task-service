package com.itm.space.taskservice.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class UpdateCommentRequest {

    @NotNull(message = "Comment ID should not be blank")
    private UUID id;

    @NotBlank(message = "Comment text should not be blank")
    @Pattern(//будет exception если текст не будет иметь мин. 10 букв на русском или на англиском.
            regexp = "^(?=(?:[^a-zA-Zа-яА-Я]*[a-zA-Zа-яА-Я]){10,}).*$" ,
            message = "Comment not have min 10 letters.")
    private String text;

    @Size(max = 3, message = "Attachments should not exceed 3 items")
    private List<UUID> attachments;
}
