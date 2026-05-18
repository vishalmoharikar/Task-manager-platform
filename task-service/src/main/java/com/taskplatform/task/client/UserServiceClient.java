package com.taskplatform.task.client;

import com.taskplatform.task.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        path = "/users",
        configuration = FeignConfig.class)
public interface UserServiceClient {
    @GetMapping("/{id}")
    Object getUserById(@PathVariable String id);
}
