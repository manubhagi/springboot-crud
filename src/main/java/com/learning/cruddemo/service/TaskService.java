package com.learning.cruddemo.service;

import com.learning.cruddemo.dto.CreateTaskRequest;
import com.learning.cruddemo.dto.CreateTaskResponse;
import com.learning.cruddemo.dto.UpdateTaskRequest;
import com.learning.cruddemo.exceptions.ApiException;
import com.learning.cruddemo.exceptions.ErrorCode;
import com.learning.cruddemo.mapper.TaskMapper;
import com.learning.cruddemo.model.Student;
import com.learning.cruddemo.model.Task;
import com.learning.cruddemo.model.TaskStatus;
import com.learning.cruddemo.repository.StudentRepo;
import com.learning.cruddemo.repository.TaskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepo taskRepo;

    private final StudentRepo studentRepo;

    // TODO : validate studentID and title

    private void validateTaskRequest(CreateTaskRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw ApiException.badRequest(ErrorCode.INVALID_TITLE, "Title cannot be null or empty");
        }
        if (request.getStudentId() == null) {
            throw ApiException.badRequest(ErrorCode.INVALID_STUDENT_ID, "Student ID must be provided");
        }
    }

    private void ensureDueDateIsFuture(LocalDate dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw ApiException.badRequest(ErrorCode.INVALID_DUE_DATE,
                    "Due date " + dueDate + " must be a future date");
        }
    }

    @Transactional
    public CreateTaskResponse createTask(CreateTaskRequest request) {
        validateTaskRequest(request);
        ensureDueDateIsFuture(request.getDueDate());

        Student student = studentRepo.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.STUDENT_NOT_FOUND,
                        "Student with id " + request.getStudentId() + " not found"));
        if (request.getStudentName() != null && !request.getStudentName().isBlank()
                && !request.getStudentName().equals(student.getStudentName())) {
            throw ApiException.badRequest(ErrorCode.STUDENT_NAME_MISMATCH,
                    "Student name does not match the provided student ID");
        }
        Task task = TaskMapper.toEntity(request, student);
        task.setTaskId(generateTaskId());
        Task saved = taskRepo.save(task);
        return TaskMapper.toResponse(saved);
    }



    public List<CreateTaskResponse> getAllTasks(Long studentId, TaskStatus status) {

        List<Task> tasks;
        if (studentId != null && status != null) {
            if (!studentRepo.existsById(studentId)) {                  // ← simplified
                throw new ApiException(HttpStatus.NOT_FOUND, ErrorCode.STUDENT_NOT_FOUND,
                        "student with id " + studentId + " not found");
            }
            tasks = taskRepo.findByStudentStudentIdAndStatus(studentId, status); // TODO : filter on due date for pending and expired status

        } else if (studentId != null) {
            if (!studentRepo.existsById(studentId)) {                  // ← simplified
                throw new ApiException(HttpStatus.NOT_FOUND, ErrorCode.STUDENT_NOT_FOUND,
                        "student with id " + studentId + " not found");
            }
            tasks = taskRepo.findByStudentStudentId(studentId);

        } else if (status != null) {
            tasks = taskRepo.findByStatus(status);

        } else {
            tasks = taskRepo.findAll();
        }

        return tasks.stream()
                .map(task -> TaskMapper.toResponse(task))
                .collect(Collectors.toList());
    }

    public CreateTaskResponse getTaskById(Long taskId) {
        Task task = taskRepo.findByTaskId(taskId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.TASK_NOT_FOUND,
                        "task with id " + taskId + " not found"));
        return TaskMapper.toResponse(task); // TODO : filter on due date for pending and expired status
    }

    public String deleteTask(Long taskId) {
        Task existing = taskRepo.findByTaskId(taskId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.TASK_NOT_FOUND,
                        "task with id " + taskId + " not found"));
        taskRepo.delete(existing);
        return "Task " + taskId + " deleted successfully";
    }

    public CreateTaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = taskRepo.findByTaskId(taskId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, ErrorCode.TASK_NOT_FOUND,
                        "task with id " + taskId + " not found"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());                           // ← setTitle not setName
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            ensureDueDateIsFuture(request.getDueDate());
            task.setDueDate(request.getDueDate());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        Task saved = taskRepo.save(task);
        return TaskMapper.toResponse(saved);
    }

    private long generateTaskId() {
        long id;
        do {
            id = ThreadLocalRandom.current().nextLong(10_000_000L, 100_000_000L);
        } while (taskRepo.existsById(id));
        return id;
    }
}
