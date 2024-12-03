package com.sunny.projectservice.controller;

import com.sunny.projectservice.dto.*;
import com.sunny.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestHeader("Authorization") String token,
                                         @RequestBody CreateRequestDto createRequestDto){

        String accessToken = token.replace("Bearer ", "");
        projectService.create(accessToken, createRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/project/{userEmail}")
    public ResponseEntity<List<ProjectResponseDto>> projectByUser(@PathVariable String userEmail){
        List<ProjectResponseDto> project = projectService.getProject(userEmail);
        return ResponseEntity.ok(project);
    }

    @PostMapping("/invite")
    public ResponseEntity<InviteResponseDto> invite(@RequestHeader("Authorization") String token,
                                                    @RequestBody InviteRequestDto inviteRequestDto){
        String accessToken = token.replace("Bearer ", "");
        projectService.invite(accessToken,inviteRequestDto);
        InviteResponseDto inviteResponseDto = new InviteResponseDto();
        inviteResponseDto.setMessage("초대 코드 발송");
        return ResponseEntity.ok(inviteResponseDto);
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<AcceptResponseDto> acceptInvite(@RequestHeader("Authorization") String token,
                                             @RequestBody AcceptRequestDto acceptRequestDto){
        String accessToken = token.replace("Bearer ", "");
        AcceptResponseDto acceptResponseDto = projectService.acceptInvite(accessToken, acceptRequestDto);
        return ResponseEntity.ok(acceptResponseDto);
    }
}
