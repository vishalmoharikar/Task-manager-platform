package com.taskplatform.task.controller;

import com.taskplatform.task.client.UserServiceClient;
import com.taskplatform.task.dto.TaskRequestDTO;
import com.taskplatform.task.entity.Task;
import com.taskplatform.task.repository.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequestDTO dto, @RequestHeader("X-User-Id") String creatorId) {
//        if (dto.getAssigneeId() != null) {
//            userServiceClient.getUserById(dto.getAssigneeId());
//        }
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus("TODO");
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM");
        task.setAssigneeId(dto.getAssigneeId());
        task.setCreatorId(creatorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskRepository.save(task));
    }

    @GetMapping
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable String id){
        return taskRepository.findById(id).orElseThrow();
    }

}
