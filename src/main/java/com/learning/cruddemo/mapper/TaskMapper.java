package com.learning.cruddemo.mapper;

import com.learning.cruddemo.dto.CreateTaskRequest;
import com.learning.cruddemo.dto.CreateTaskResponse;
import com.learning.cruddemo.model.Task;
import com.learning.cruddemo.model.TaskStatus;
import com.learning.cruddemo.model.Student;
import java.time.LocalDate;

public class TaskMapper {

    public static Task toEntity(CreateTaskRequest request, Student student) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .student(student)
                .status(TaskStatus.PENDING)
                .build();
    }

    public static CreateTaskResponse toResponse(Task task) {
        Student student = task.getStudent();
        return CreateTaskResponse.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(resolveStatus(task))
                .studentId(student != null ? student.getStudentId() : null)
                .studentName(student != null ? student.getStudentName() : null)
                .build();
    }

    public static TaskStatus resolveStatus(Task task) {
        TaskStatus stored = task.getStatus() == null ? TaskStatus.PENDING : task.getStatus();
        if (isExpired(task) && stored != TaskStatus.COMPLETED) {
            return TaskStatus.EXPIRED;
        }
        return stored;
    }

    public static boolean isExpired(Task task) {
        return task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now());
    }
}
