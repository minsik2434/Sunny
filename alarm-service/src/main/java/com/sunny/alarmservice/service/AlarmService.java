package com.sunny.alarmservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AlarmService {
    SseEmitter connection(String accessToken, String lastEventId, HttpServletResponse httpServletResponse);

    void send(String id, String content);
}
