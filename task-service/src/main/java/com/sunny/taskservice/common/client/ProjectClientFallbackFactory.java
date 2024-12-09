package com.sunny.taskservice.common.client;

import com.sunny.taskservice.dto.ProjectMemberDto;
import com.sunny.taskservice.exception.ResourceNotFoundException;
import com.sunny.taskservice.exception.ServiceUnavailable;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;

public class ProjectClientFallbackFactory implements FallbackFactory<ProjectClient> {
    @Override
    public ProjectClient create(Throwable cause) {
        return new ProjectClient() {
            @Override
            public ProjectMemberDto getProjectMember(Long projectId, String userEmail) {
                if(cause instanceof FeignException && ((FeignException) cause).status() == 404){
                    throw new ResourceNotFoundException("Cannot find users belonging to project");
                }
                throw new ServiceUnavailable("Unavailable ProjectService");
            }
        };
    }
}
