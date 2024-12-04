package com.sunny.alarmservice.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void deleteById(String emitterId);

    Map<String,SseEmitter> findAllEmitterByUserEmail(String userEmail);
}
