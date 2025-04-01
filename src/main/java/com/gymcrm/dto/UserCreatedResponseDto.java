package com.gymcrm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserCreatedResponseDto implements Serializable {

    @NotNull(message = "Username is required")
    private String username;

    @NotNull(message = "Password is required")
    private String password;
}
