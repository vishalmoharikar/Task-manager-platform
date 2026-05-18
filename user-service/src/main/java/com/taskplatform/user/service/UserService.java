package com.taskplatform.user.service;

import com.taskplatform.user.entity.User;
import com.taskplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private  final UserRepository userRepository;

    @Cacheable(value="users", key = "#id")
    public Optional<User> findById(String id){
        System.out.println("Cache MISS - fetching from MongoDB for id: \" + id");
        return userRepository.findById(id);
    }

    @Cacheable(value = "users", key = "#username")
    public Optional<User> findByUserName(String username){
        System.out.println("Cache MISS - fetching from MongoDB for username: " + username);
        return userRepository.findByUsername(username);
    }

    @CacheEvict(value = "users", allEntries = true)
    public User save(User user) {
        System.out.println("Cache EVICT - clearing all users cache");
        return userRepository.save(user);
    }
}
