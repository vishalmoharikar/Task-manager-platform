package com.taskplatform.task.event;

import java.io.Serializable;

public record TaskCreatedEvent(
        String taskId,
        String title,
        String description,
        String status,
        String priority,
        String assigneeId,
        String creatorId
) implements Serializable {}