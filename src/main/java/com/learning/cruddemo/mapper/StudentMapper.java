package com.learning.cruddemo.mapper;

import com.learning.cruddemo.dto.CreateStudentRequest;
import com.learning.cruddemo.dto.CreateStudentResponse;
import com.learning.cruddemo.model.Student;

public class StudentMapper {

    public static Student toEntity(CreateStudentRequest request) {
        return Student.builder()
                .studentName(request.getStudentName())
                .rollNumber(request.getRollNumber())
                .email(request.getEmail())
                .build();
    }

    public static CreateStudentResponse toResponse(Student student) {
        return CreateStudentResponse.builder()
                .studentId(student.getStudentId())
                .studentName(student.getStudentName())
                .rollNumber(student.getRollNumber())
                .email(student.getEmail())
                .build();
    }
}
