package com.taskplatform.notification.consumer;

import com.taskplatform.notification.event.TaskCreatedEvent;
import com.taskplatform.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService;

    @RabbitListener(
            queues = "${rabbitmq.queue.notification:notification.queue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void handleTaskCreatedEvent(TaskCreatedEvent event) {
        log.info("Received event for taskId: {}", event.getTaskId());
        notificationService.processTaskCreatedNotification(event);
    }

}
