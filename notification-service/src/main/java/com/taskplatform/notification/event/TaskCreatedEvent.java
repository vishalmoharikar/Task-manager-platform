package com.taskplatform.notification.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // safe — ignore extra fields
public class TaskCreatedEvent {
    private String taskId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String assigneeId;
    private String creatorId;
}