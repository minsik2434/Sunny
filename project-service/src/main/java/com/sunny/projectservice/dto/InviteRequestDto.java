package com.sunny.projectservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteRequestDto {
    private Long projectId;
    private String toInviteEmail;
}
