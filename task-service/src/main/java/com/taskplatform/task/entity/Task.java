package com.taskplatform.task.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection =  "tasks")
public class Task {
    @Id
    private String id;
    private String title;
    private String description;
    private String status;  // TODO, IN_PROGRESS, DONE
    private String priority; // LOW, MEDIUM, HIGH
    private String assigneeId;
    private String creatorId;

}
