package com.sunny.taskservice.common.client;

import com.sunny.taskservice.dto.ProjectMemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "project-service", url = "${feign-endpoint}", fallbackFactory = ProjectClientFallbackFactory.class)
public interface ProjectClient {
    @GetMapping("/project/{projectId}/members")
    ProjectMemberDto getProjectMember(@PathVariable Long projectId, @RequestParam("email") String userEmail);
}
