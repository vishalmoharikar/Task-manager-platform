package com.taskplatform.notification.service;

import com.taskplatform.notification.event.TaskCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void processTaskCreatedNotification(TaskCreatedEvent event) {
        // For now: structured console log
        // Later: replace with email/SMS/push notification
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📬 NEW TASK NOTIFICATION");
        log.info("   Task ID   : {}", event.getTaskId());
        log.info("   Title     : {}", event.getTitle());
        log.info("   Priority  : {}", event.getPriority());
        log.info("   Status    : {}", event.getStatus());
        log.info("   Assignee  : {}", event.getAssigneeId());
        log.info("   Creator   : {}", event.getCreatorId());
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
