package com.taskplatform.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequestDTO {
    @NotBlank
    private String title;
    private String description;
    private String priority;
    private String assigneeId;
}
