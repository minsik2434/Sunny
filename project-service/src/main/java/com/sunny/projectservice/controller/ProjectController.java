package com.sunny.projectservice.controller;

import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.dto.ProjectResponseDto;
import com.sunny.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
}
