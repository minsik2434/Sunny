package com.sunny.alarmservice.service;

import com.sunny.alarmservice.common.JwtUtil;
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
    private static final Long EMITTER_TIME = 120L * 1000 * 60;
    @Override
    public SseEmitter connection(String accessToken, String lastEventId, HttpServletResponse httpServletResponse) {
        String userId = jwtUtil.getClaim(accessToken);

        String emitterId = createEmitterId(userId);
        SseEmitter sseEmitter = new SseEmitter(EMITTER_TIME);

        SseEmitter emitter = emitterRepository.save(emitterId, sseEmitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        sendEmitter(emitter,"eventId", emitterId, "data");

        return emitter;

    }

    @Override
    public void send(String userEmail, String content) {
        Map<String, SseEmitter> sseEmitterMap = emitterRepository.findAllEmitterByUserEmail(userEmail);
        sseEmitterMap.forEach(
                (id, emitter) -> {
                    sendEmitter(emitter, "eventId", id, "data");
                }
        );

    }

    private String createEmitterId(String userEmail){
        return userEmail + "_" + System.currentTimeMillis();
    }
    private void sendEmitter(SseEmitter emitter, String eventId, String emitterId, Object data){
        try{
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException e){
            emitterRepository.deleteById(emitterId);
        }
    }
}
