package com.sunny.alarmservice.service;

import com.sunny.alarmservice.common.JwtUtil;
import com.sunny.alarmservice.domain.Alarm;
import com.sunny.alarmservice.dto.AlarmDto;
import com.sunny.alarmservice.repository.AlarmRepository;
import com.sunny.alarmservice.repository.EmitterRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService{

    private final JwtUtil jwtUtil;
    private final EmitterRepository emitterRepository;
    private final AlarmRepository alarmRepository;
    private static final Long EMITTER_TIME = 6000000L;
    @Override
    public SseEmitter connection(String accessToken, String lastEventId, HttpServletResponse httpServletResponse) {
        String userEmail = jwtUtil.getClaim(accessToken);

        String emitterId = createEmitterId(userEmail);
        SseEmitter sseEmitter = new SseEmitter(EMITTER_TIME);

        SseEmitter emitter = emitterRepository.save(emitterId, sseEmitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        sendEmitter(emitter, emitterId,"connection", "connected");

        if(!lastEventId.isEmpty()){
            Map<String, Object> caches = emitterRepository.findAllEventCacheByUserEmail(userEmail);
            caches.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendEmitter(emitter, entry.getKey(), "LostData", entry.getValue()));
        }
        return emitter;
    }

    @Override
    public void send(String userEmail, String type, String content, Long sender) {
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterByUserEmail(userEmail);
        Alarm alarm = new Alarm(type, sender, userEmail, content);
        Alarm savedAlarm = alarmRepository.save(alarm);

        emitters.forEach(
                (id, emitter) -> {
                    emitterRepository.saveEventCache(id, savedAlarm);
                    sendEmitter(emitter, id, type, new AlarmDto(savedAlarm));
                }
        );
    }

    private String createEmitterId(String userEmail){
        return userEmail + "_" + System.currentTimeMillis();
    }
    private void sendEmitter(SseEmitter emitter, String emitterId, String emitterName, Object data){
        try{
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name(emitterName)
                    .data(data));
        } catch (IOException e){
            emitterRepository.deleteById(emitterId);
        }
    }
}
