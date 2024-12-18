package com.sunny.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AcceptResponseDto {
    private String projectName;
    private String email;
    private String role;
}
