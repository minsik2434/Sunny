package com.sunny.projectservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptRequestDto {
    private Long projectId;
    private String inviteCode;
}
