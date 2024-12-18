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
public class CreateSubTaskRequestDto {
    private Long projectId;
    @NotNull
    private Long mainTaskId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String status;
    @UniqueElements
    private List<String> assignedMembers;
}
