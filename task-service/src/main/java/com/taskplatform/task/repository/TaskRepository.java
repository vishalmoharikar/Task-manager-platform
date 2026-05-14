package com.taskplatform.task.repository;

import com.taskplatform.task.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task,String> {
    List<Task> findByAssigneeId(String assigneeId);
}
