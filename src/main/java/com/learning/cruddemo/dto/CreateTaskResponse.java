package com.learning.cruddemo.dto;

import com.learning.cruddemo.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateTaskResponse {
    private Long taskId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long studentId;
    private String studentName;
}
