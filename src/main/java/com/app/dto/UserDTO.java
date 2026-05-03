package com.app.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
public class UserDTO {

    private Long id;

    @NotBlank(message = "Name required")
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 5)
    private String password;

    private String role;
}