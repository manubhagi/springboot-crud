package com.learning.cruddemo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateStudentResponse {
    private Long studentId;
    private String studentName;
    private long rollNumber;
    private String email;
}
