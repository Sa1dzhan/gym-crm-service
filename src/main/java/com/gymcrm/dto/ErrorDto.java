package com.gymcrm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorDto {
    private Date timestamp;
    private String message;
    private String details;
}
