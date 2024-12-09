package com.sunny.taskservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_task_id")
    private SubTask subTask;

    public TaskMember(String email, SubTask subTask){
        this.email = email;
        this.subTask = subTask;
        subTask.getTaskMemberList().add(this);
    }
}
