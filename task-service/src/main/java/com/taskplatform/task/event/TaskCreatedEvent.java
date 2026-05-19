package com.taskplatform.task.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("TaskCreatedEvent")
public class TaskCreatedEvent {
    private String taskId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String assigneeId;
    private String creatorId;
}