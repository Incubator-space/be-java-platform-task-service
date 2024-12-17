package com.itm.space.taskservice.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTTaskCheckResponse {

    private String result;

    private String reason;
}
