package com.sunny.taskservice.dto;

import com.sunny.taskservice.common.annotation.validation.UniqueElements;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AssignMemberRequestDto {
    @NotNull
    private Long projectId;
    @UniqueElements
    private List<String> assignedMembers;
}
