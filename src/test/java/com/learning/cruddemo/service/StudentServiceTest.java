package com.learning.cruddemo.service;


import static org.mockito.ArgumentMatchers.anyLong;
import java.util.Optional;
import com.learning.cruddemo.dto.CreateStudentRequest;
import com.learning.cruddemo.dto.CreateStudentResponse;
import com.learning.cruddemo.exceptions.ApiException;
import com.learning.cruddemo.model.Student;
import com.learning.cruddemo.repository.StudentRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepo studentRepo;

    @InjectMocks
    private StudentService studentService;

    @Test
    void createStudent_shouldReturnResponse_whenValidRequest() {

        // given
        CreateStudentRequest request = new CreateStudentRequest();
        request.setStudentName("Arjun Kumar");
        request.setRollNumber(2201);
        request.setEmail("arjun@college.edu");

        Student savedStudent = new Student();
        savedStudent.setStudentName("Arjun Kumar");
        savedStudent.setRollNumber(2201);
        savedStudent.setEmail("arjun@college.edu");

        when(studentRepo.existsByEmail("arjun@college.edu")).thenReturn(false);
        when(studentRepo.existsByRollNumber(2201)).thenReturn(false);
        when(studentRepo.save(any(Student.class))).thenReturn(savedStudent);

        // when
        CreateStudentResponse response = studentService.createStudent(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStudentName()).isEqualTo("Arjun Kumar");
        assertThat(response.getEmail()).isEqualTo("arjun@college.edu");
    }

    @Test
    void createStudent_shouldThrowDuplicateException_whenEmailExists() {

        // given
        CreateStudentRequest request = new CreateStudentRequest();
        request.setStudentName("Arjun Kumar");
        request.setRollNumber(2201);
        request.setEmail("arjun@college.edu");

        when(studentRepo.existsByEmail("arjun@college.edu")).thenReturn(true); // ← email exists!

        // when + then
        assertThatThrownBy(() -> studentService.createStudent(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createStudent_shouldThrowDuplicateException_whenRollNumberExists() {

        // given
        CreateStudentRequest request = new CreateStudentRequest();
        request.setStudentName("Arjun Kumar");
        request.setRollNumber(2201);
        request.setEmail("arjun@college.edu");

        when(studentRepo.existsByEmail("arjun@college.edu")).thenReturn(false);
        when(studentRepo.existsByRollNumber(2201)).thenReturn(true); // ← roll exists!

        // when + then
        assertThatThrownBy(() -> studentService.createStudent(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createStudent_shouldThrowValidationException_whenNameIsEmpty() {

        // given
        CreateStudentRequest request = new CreateStudentRequest();
        request.setStudentName("");  // ← empty name!
        request.setRollNumber(2201);
        request.setEmail("arjun@college.edu");

        // when + then
        assertThatThrownBy(() -> studentService.createStudent(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Name cannot be null or empty");
    }


    @Test
    void createStudent_shouldThrowValidationException_whenEmailIsInvalid() {

        // given
        CreateStudentRequest request = new CreateStudentRequest();
        request.setStudentName("Arjun Kumar");
        request.setRollNumber(2201);
        request.setEmail("invalidemail");  // ← bad email!

        // when + then
        assertThatThrownBy(() -> studentService.createStudent(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    void getStudentById_shouldReturnStudent_whenStudentExists() {

        // given
        Student student = new Student();
        student.setStudentId(26787653L);
        student.setStudentName("Arjun Kumar");
        student.setRollNumber(2201);
        student.setEmail("manu@gmail.com");

        when(studentRepo.findByStudentId(anyLong())).thenReturn(Optional.of(student));

        // when
        CreateStudentResponse response = studentService.getStudentById(26787653L);

        // then
        assertThat(response.getStudentName()).isEqualTo("Arjun Kumar");
        assertThat(response.getEmail()).isEqualTo("manu@gmail.com");
        assertThat(response.getRollNumber()).isEqualTo(2201);
    }

    @Test
    void getStudentById_shouldThrowException_whenStudentNotFound() {

        // given
        when(studentRepo.findByStudentId(anyLong())).thenReturn(Optional.empty()); // ← no student!

        // when + then
        assertThatThrownBy(() -> studentService.getStudentById(999L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");
    }


}
