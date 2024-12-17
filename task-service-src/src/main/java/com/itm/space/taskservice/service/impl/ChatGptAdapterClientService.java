package com.itm.space.taskservice.service.impl;

import com.itm.space.taskservice.api.request.ChatGPTTaskCheckRequest;
import com.itm.space.taskservice.api.response.ChatGPTTaskCheckResponse;
import com.itm.space.taskservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGptAdapterClientService {

    private final WebClient adapterWebClient;

    private final SecurityUtil securityUtil;

    public ChatGPTTaskCheckResponse sendRequestToChatGPT(ChatGPTTaskCheckRequest request) {

        String token = securityUtil.getToken();

        return adapterWebClient.post()
                .uri("/task/check")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatGPTTaskCheckResponse.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                        .filter(throwable ->
                            throwable instanceof WebClientResponseException webClientException &&
                                webClientException.getStatusCode().is5xxServerError()
                        )
                        .doBeforeRetry(retrySignal -> log.warn("Ошибка при отправке запроса на ChatGPT")))
                .block();
    }
}
