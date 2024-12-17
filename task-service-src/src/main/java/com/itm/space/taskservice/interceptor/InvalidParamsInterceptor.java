package com.itm.space.taskservice.interceptor;

import jakarta.annotation.Nullable;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class InvalidParamsInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @Nullable HttpServletResponse response,
                             @Nullable Object handler
    ) throws Exception {

        if (request.getDispatcherType() != DispatcherType.ERROR) {
            return true;
        }

        String query = request.getQueryString();

        if (query != null) {
            try {
                URLDecoder.decode(query, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException e) {
                if (response == null) {
                    return true;
                }
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("""
                    {    \s
                    "code": 400,    \s
                    "type": "Bad Request",    \s
                    "message": "Неправильные параметры запроса"\s
                    }""");

                return false;
            }
        }

        return true;
    }
}
