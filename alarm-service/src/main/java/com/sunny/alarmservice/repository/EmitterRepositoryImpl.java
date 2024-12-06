package com.sunny.alarmservice.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository{
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();
    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void deleteById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterByUserEmail(String userEmail) {
        return emitters.entrySet().stream()
                .filter(entry-> entry.getKey().startsWith(userEmail))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    @Override
    public Map<String, Object> findAllEventCacheByUserEmail(String userEmail) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userEmail))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteAllEmitterByUserEmail(String userEmail) {
        List<String> list = emitters.keySet().stream().filter(key -> key.startsWith(userEmail)).toList();
        list.forEach(emitters::remove);
    }

    @Override
    public void deleteAllEventCacheByUserEmail(String userEmail) {
        List<String> list = eventCache.keySet().stream().filter(key -> key.startsWith(userEmail)).toList();
        list.forEach(eventCache::remove);
    }
}
