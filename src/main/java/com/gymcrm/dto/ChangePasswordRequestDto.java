package com.gymcrm.dto;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String oldPassword;
    private String newPassword;
}
