package com.sunny.projectservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestDto {
    private String projectName;
    private String description;
}
