package com.itm.space.taskservice.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatGPTTaskCheckRequest {

    @NotBlank(message = "Task description must not be provided and empty")
    private String task;

    @NotBlank(message = "Task description must not be provided and empty")
    private String solution;
}
