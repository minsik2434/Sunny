package com.sunny.userservice.client;

import com.sunny.userservice.dto.ProjectResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("project-service")
public interface ProjectClient {

    @GetMapping("/project/{userEmail}")
    List<ProjectResponseDto> getProjectList(@PathVariable String userEmail);
}
