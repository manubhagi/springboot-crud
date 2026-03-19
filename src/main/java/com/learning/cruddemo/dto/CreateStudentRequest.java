package com.learning.cruddemo.dto;

import lombok.Data;

@Data
public class CreateStudentRequest {
    private String studentName;
    private long rollNumber;
    private String email;
}