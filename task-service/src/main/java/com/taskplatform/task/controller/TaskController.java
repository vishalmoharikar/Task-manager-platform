package com.taskplatform.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskplatform.task.client.UserServiceClient;
import com.taskplatform.task.config.RabbitConfig;
import com.taskplatform.task.dto.TaskRequestDTO;
import com.taskplatform.task.entity.Task;
import com.taskplatform.task.event.TaskCreatedEvent;
import com.taskplatform.task.exception.TaskNotFoundException;
import com.taskplatform.task.exception.UserServiceException;
import com.taskplatform.task.repository.TaskRepository;
import feign.FeignException;
 import jakarta.validation.Valid;
 import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;
    private final RabbitTemplate rabbitTemplate;

    public TaskController(TaskRepository taskRepository, UserServiceClient userServiceClient, @Qualifier("rabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.taskRepository = taskRepository;
        this.userServiceClient = userServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequestDTO dto, @RequestHeader("X-User-Id") String creatorId) {
        try {
            if (dto.getAssigneeId() != null) {
                userServiceClient.getUserById(dto.getAssigneeId());
            }
        } catch (FeignException.NotFound e) {
            throw new UserServiceException("User not found: " + dto.getAssigneeId());
        } catch (feign.FeignException e) {
            throw new UserServiceException("User Service unavailable");
        }
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus("TODO");
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM");
        task.setAssigneeId(dto.getAssigneeId());
        task.setCreatorId(creatorId);

        Task savedTask = taskRepository.save(task);

        publishTask(savedTask);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);

    }

    private void publishTask(Task savedTask) {
         try {
            TaskCreatedEvent event = new TaskCreatedEvent(
                    savedTask.getId(),
                    savedTask.getTitle(),
                    savedTask.getDescription(),
                    savedTask.getStatus(),
                    savedTask.getPriority(),
                    savedTask.getAssigneeId(),
                    savedTask.getCreatorId()
            );
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY,
                    event
            );
            log.info("Task event published: {}", savedTask.getId());
        } catch (Exception e) {
            log.error("Failed to publish event for taskId: {}. Error: {}",
                    savedTask.getId(), e.getMessage());
        }
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable String id) {
        return taskRepository.findById(id).orElseThrow(()->new TaskNotFoundException(id));
    }


}
