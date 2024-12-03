package com.sunny.projectservice.service;

import com.sunny.projectservice.dto.*;

import java.util.List;

public interface ProjectService {
    Long create(String accessToken, CreateRequestDto createRequestDto);

    List<ProjectResponseDto> getProject(String userEmail);

    void invite(String accessToken, InviteRequestDto inviteRequestDto);

    AcceptResponseDto acceptInvite(String accessToken, AcceptRequestDto acceptRequestDto);
}
