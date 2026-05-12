package com.taskplatform.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Singular;

@Data
public class UserRequestDTO {
    @NotBlank (message = "Username is required")
    private String username;

    @NotBlank (message = "Username is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Passworld is required")
    @Size (min = 6, message = "Password must be at least 6 characters")
    private String password;
}
