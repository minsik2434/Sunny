package com.sunny.projectservice.controller;

import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestHeader("Authorization") String token,
                                         @RequestBody CreateRequestDto createRequestDto){

        String accessToken = token.replace("Bearer ", "");
        projectService.create(accessToken, createRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
