package com.sunny.taskservice.domain;

import com.sunny.taskservice.dto.CreateSubTaskRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_task_id")
    private MainTask mainTask;

    @OneToMany(mappedBy = "subTask", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<TaskMember> taskMemberList = new ArrayList<>();

    public SubTask(MainTask mainTask, String title, String description, String status){
        this.title = title;
        this.description = description;
        this.status = status;
        this.mainTask = mainTask;
    }
}
