package com.sunny.taskservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProjectMemberDto {
    private Long projectId;
    private String userEmail;
    private String role;
}
