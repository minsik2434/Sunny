package com.sunny.taskservice.controller;

import com.sunny.taskservice.dto.AssignMemberRequestDto;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import com.sunny.taskservice.dto.CreateSubTaskRequestDto;
import com.sunny.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    @PostMapping("/mainTask")
    public ResponseEntity<Void> createMainTask(@RequestHeader("Authorization") String token,
                                           @RequestBody @Validated CreateMainTaskRequestDto createMainTaskRequestDto){
        String accessToken = token.replace("Bearer ", "");
        taskService.mainTaskSave(accessToken, createMainTaskRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/subTask")
    public ResponseEntity<Void> createSubTask(@RequestHeader("Authorization") String token,
                                              @RequestBody @Validated CreateSubTaskRequestDto createSubTaskRequestDto){
        String accessToken = token.replace("Bearer ", "");
        taskService.subTaskSave(accessToken, createSubTaskRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/subTask/{subTaskId}/addMember")
    public ResponseEntity<Void> addAssignMember(@RequestHeader("Authorization") String token,
                                                @PathVariable Long subTaskId,
                                                @RequestBody @Validated AssignMemberRequestDto assignMemberRequestDto){
        String accessToken = token.replace("Bearer ", "");
        taskService.addAssignMember(accessToken,subTaskId, assignMemberRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
