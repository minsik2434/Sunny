package com.sunny.taskservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MainTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime deadline;
    private Long projectId;

    @OneToMany(mappedBy = "mainTask", orphanRemoval = true)
    private List<SubTask> subTaskList = new ArrayList<>();

    public MainTask(String name, String description, LocalDateTime startDate, LocalDateTime deadLine, Long projectId){
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.deadline = deadLine;
        this.projectId = projectId;
    }

}
