package com.sunny.alarmservice.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void deleteById(String emitterId);

    Map<String,SseEmitter> findAllEmitterByUserEmail(String userEmail);
    void saveEventCache(String eventCacheId, Object event);

    Map<String, Object> findAllEventCacheByUserEmail(String userEmail);

    void deleteAllEmitterByUserEmail(String userEmail);

    void deleteAllEventCacheByUserEmail(String userEmail);
}
