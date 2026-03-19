package com.learning.cruddemo.service;

import com.learning.cruddemo.exceptions.ApiException;
import com.learning.cruddemo.exceptions.ErrorCode;
import com.learning.cruddemo.dto.CreateStudentRequest;
import com.learning.cruddemo.dto.CreateStudentResponse;
import com.learning.cruddemo.mapper.StudentMapper;
import com.learning.cruddemo.model.Student;
import com.learning.cruddemo.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepo studentRepo;

    private void validateStudentRequest(CreateStudentRequest request) {
        // Name validation
        if (request.getStudentName() == null || request.getStudentName().isBlank()) {
            throw ApiException.badRequest(ErrorCode.INVALID_NAME, "Name cannot be null or empty");
        }
        if (request.getStudentName().length() > 20) {
            throw ApiException.badRequest(ErrorCode.INVALID_NAME, "Name cannot exceed 20 characters");
        }

        // Roll number validation
        if (request.getRollNumber() == 0) {
            throw ApiException.badRequest(ErrorCode.INVALID_ROLL_NUMBER, "Roll number cannot be null or zero");
        }
        String rollStr = String.valueOf(request.getRollNumber());
        if (rollStr.length() != 4) {
            throw ApiException.badRequest(ErrorCode.INVALID_ROLL_NUMBER, "Roll number must be exactly 4 digits");
        }

        // Email validation
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw ApiException.badRequest(ErrorCode.INVALID_EMAIL, "Email cannot be null or empty");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw ApiException.badRequest(ErrorCode.INVALID_EMAIL, "Invalid email format");
        }
    }
    // TODO: validate name, roll no, email present in request
    @Transactional
    public CreateStudentResponse createStudent(CreateStudentRequest request) {
        validateStudentRequest(request);


        if (studentRepo.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.DUPLICATE_EMAIL,
                    "Email " + request.getEmail() + " already exists");
        }
        if (studentRepo.existsByRollNumber(request.getRollNumber())) {
            throw new ApiException(HttpStatus.CONFLICT, ErrorCode.DUPLICATE_ROLL_NUMBER,
                    "Roll number " + request.getRollNumber() + " already exists");
        }

        Student student = StudentMapper.toEntity(request);
        student.setStudentId(generateStudentId());
        Student saved = studentRepo.save(student);
        return StudentMapper.toResponse(saved);
    }

    public CreateStudentResponse getStudentById(Long studentId) {
        Student student = studentRepo.findByStudentId(studentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.STUDENT_NOT_FOUND,
                        "Student with id " + studentId + " not found"));
        return StudentMapper.toResponse(student);
    }

    private long generateStudentId() {
        long id;
        do {
            id = ThreadLocalRandom.current().nextLong(10_000_000L, 100_000_000L);
        } while (studentRepo.existsById(id));
        return id;
    }
}
