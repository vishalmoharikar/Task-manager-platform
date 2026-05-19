package com.taskplatform.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskplatform.task.client.UserServiceClient;
import com.taskplatform.task.config.RabbitConfig;
import com.taskplatform.task.dto.TaskRequestDTO;
import com.taskplatform.task.entity.Task;
import com.taskplatform.task.event.TaskCreatedEvent;
import com.taskplatform.task.repository.TaskRepository;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserServiceClient userServiceClient;
    private final RabbitTemplate rabbitTemplate;

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequestDTO dto, @RequestHeader("X-User-Id") String creatorId) {
        try {
            if (dto.getAssigneeId() != null) {
                userServiceClient.getUserById(dto.getAssigneeId());
            }
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("User not found: " + dto.getAssigneeId());
        } catch (feign.FeignException e) {
            throw new RuntimeException("User Service unavailable");
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
        //now publishing Task
        try {
            System.out.println("PUBLISHING EVENT TO RABBITMQ: " + savedTask.getId());
            TaskCreatedEvent event = new TaskCreatedEvent(savedTask.getId(),
                    savedTask.getTitle(),
                    savedTask.getDescription(),
                    savedTask.getStatus(),
                    savedTask.getPriority(),
                    savedTask.getAssigneeId(),
                    savedTask.getCreatorId()
            );
            System.out.println("Event object: " + event);
            System.out.println("Event taskId: " + event.getTaskId());
            System.out.println("Event title: " + event.getTitle());

            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(event);
                System.out.println("JSON STRING: " + json);
            } catch (Exception e) {
                System.out.println("JSON SERIALIZATION FAILED: " + e.getMessage());
            }

            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, event);
            System.out.println("EVENT PUBLISHED SUCCESSFULLY");
        } catch (Exception e) {
            System.out.println("FAILED TO PUBLISH: " + e.getMessage());
            e.printStackTrace();
        }


    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable String id) {
        return taskRepository.findById(id).orElseThrow();
    }


}
