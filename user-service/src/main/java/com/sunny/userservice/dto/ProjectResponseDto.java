package com.sunny.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProjectResponseDto {
    private Long ProjectId;
    private String projectName;
    private String projectDescription;
    private String role;
}
