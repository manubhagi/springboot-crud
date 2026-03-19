package com.learning.cruddemo.dto;

import com.learning.cruddemo.model.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {
    private String title; // TODO: title
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
}
