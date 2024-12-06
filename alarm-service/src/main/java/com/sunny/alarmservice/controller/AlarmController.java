package com.sunny.alarmservice.controller;

import com.sunny.alarmservice.dto.AlarmRequestDto;
import com.sunny.alarmservice.service.AlarmService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
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

    @PostMapping("/alarm")
    public ResponseEntity<Void> alarm(@RequestBody AlarmRequestDto alarmRequestDto){
        log.info("alarm 호출");
        alarmService.send(alarmRequestDto.getRecipientEmail(),
                alarmRequestDto.getType(),
                alarmRequestDto.getContent(),
                alarmRequestDto.getProjectId());
        return ResponseEntity.ok().build();
    }
}
