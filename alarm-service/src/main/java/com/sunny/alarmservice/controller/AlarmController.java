package com.sunny.alarmservice.controller;

import com.sunny.alarmservice.service.AlarmService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> sseConnection(@RequestHeader("Authorization") String token,
                                                    @RequestHeader(value = "Last-Event-Id",
                                                            required = false, defaultValue = "") String lastEventId,
                                                    HttpServletResponse response){
        String accessToken = token.replace("Bearer", "");
        SseEmitter emitter = alarmService.connection(accessToken, lastEventId, response);
        return ResponseEntity.ok(emitter);
    }

    @GetMapping("/test")
    public ResponseEntity<Void> notification(){
        alarmService.send("test@naver.com","data");
        return ResponseEntity.ok().build();
    }
}
