package com.taskplatform.user.controller;

import com.taskplatform.user.dto.UserRequestDTO;
import com.taskplatform.user.dto.UserResponseDTO;
import com.taskplatform.user.entity.User;
import com.taskplatform.user.repository.UserRepository;
import com.taskplatform.user.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO requestDTO){
        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        var user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        var saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponseDTO.builder()
                        .id(saved.getId())
                        .username(saved.getUsername())
                        .email(saved.getEmail())
                        .build());
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String>request){
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Invalid credentials"));

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        var response = new HashMap<String, String>();
        response.put("token", token);
        response.put("type", "Bearer");
        return ResponseEntity.ok(response);
    }

 /*   @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO requestDTO) {
        var user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        var saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponseDTO.builder()
                        .id(saved.getId())
                        .username(saved.getUsername())
                        .email(saved.getEmail())
                        .build());
    }*/

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user ->
                UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .toList();
    }
}
