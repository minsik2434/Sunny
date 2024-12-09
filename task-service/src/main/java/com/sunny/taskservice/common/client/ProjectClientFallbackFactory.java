package com.sunny.taskservice.common.client;

import com.sunny.taskservice.dto.ProjectMemberDto;
import com.sunny.taskservice.exception.ResourceNotFoundException;
import com.sunny.taskservice.exception.ServiceUnavailable;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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
