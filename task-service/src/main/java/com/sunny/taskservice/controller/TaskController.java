package com.sunny.taskservice.controller;

import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    @PostMapping("/task")
    public ResponseEntity<Void> createTask(@RequestHeader("Authorization") String token,
                                           @RequestBody CreateMainTaskRequestDto createMainTaskRequestDto){
        String accessToken = token.replace("Bearer ", "");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
