package com.learning.cruddemo.service;

import com.learning.cruddemo.dto.CreateTaskRequest;
import com.learning.cruddemo.dto.CreateTaskResponse;
import com.learning.cruddemo.dto.UpdateTaskRequest;
import com.learning.cruddemo.exceptions.ApiException;
import com.learning.cruddemo.model.Student;
import com.learning.cruddemo.model.Task;
import com.learning.cruddemo.model.TaskStatus;
import com.learning.cruddemo.repository.StudentRepo;
import com.learning.cruddemo.repository.TaskRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepo taskRepo;

    @Mock
    private StudentRepo studentRepo;

    @InjectMocks
    private TaskService taskService;


    // ─────────────────────────────────────────────────────────────
    // CREATE TASK
    // ─────────────────────────────────────────────────────────────

    @Test
    void createTask_shouldReturnResponse_whenValidRequest() {

        // given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Study Spring Boot");
        request.setDescription("Finish CRUD project");
        request.setDueDate(LocalDate.now().plusDays(5));
        request.setStudentId(1001L);

        Student student = new Student();
        student.setStudentId(1001L);
        student.setStudentName("John Doe");

        Task savedTask = new Task();
        savedTask.setTaskId(55123456L);
        savedTask.setTitle("Study Spring Boot");
        savedTask.setDescription("Finish CRUD project");
        savedTask.setDueDate(LocalDate.now().plusDays(5));
        savedTask.setStudent(student);
        savedTask.setStatus(TaskStatus.PENDING);

        when(studentRepo.findByStudentId(1001L)).thenReturn(Optional.of(student));
        when(taskRepo.save(any(Task.class))).thenReturn(savedTask);

        // when
        CreateTaskResponse response = taskService.createTask(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Study Spring Boot");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.getStudentId()).isEqualTo(1001L);
    }

    @Test
    void createTask_shouldThrowValidationException_whenTitleIsEmpty() {

        // given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("");  // empty title
        request.setStudentId(1001L);
        request.setDueDate(LocalDate.now().plusDays(5));

        // when + then
        assertThatThrownBy(() -> taskService.createTask(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Title cannot be null or empty");
    }

    @Test
    void createTask_shouldThrowValidationException_whenTitleIsNull() {

        // given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle(null);  // null title
        request.setStudentId(1001L);
        request.setDueDate(LocalDate.now().plusDays(5));

        // when + then
        assertThatThrownBy(() -> taskService.createTask(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Title cannot be null or empty");
    }

    @Test
    void createTask_shouldThrowValidationException_whenStudentIdIsNull() {

        // given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Study Spring Boot");
        request.setStudentId(null);  // null studentId
        request.setDueDate(LocalDate.now().plusDays(5));

        // when + then
        assertThatThrownBy(() -> taskService.createTask(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Student ID must be provided");
    }

    @Test
    void createTask_shouldThrowInvalidDueDateException_whenDueDateIsInPast() {

        // given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Study Spring Boot");
        request.setStudentId(1001L);
        request.setDueDate(LocalDate.now().minusDays(1));  // yesterday

        // when + then
        assertThatThrownBy(() -> taskService.createTask(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("must be a future date");
    }

    @Test
    void createTask_shouldThrowResourceNotFound_whenStudentDoesNotExist() {

        // given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Study Spring Boot");
        request.setStudentId(999L);
        request.setDueDate(LocalDate.now().plusDays(5));

        when(studentRepo.findByStudentId(999L)).thenReturn(Optional.empty());  // student not found

        // when + then
        assertThatThrownBy(() -> taskService.createTask(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void createTask_shouldSetStatusToPending_always() {

        // given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Study Spring Boot");
        request.setStudentId(1001L);
        request.setDueDate(LocalDate.now().plusDays(5));

        Student student = new Student();
        student.setStudentId(1001L);
        student.setStudentName("John Doe");

        Task savedTask = new Task();
        savedTask.setTaskId(55123456L);
        savedTask.setTitle("Study Spring Boot");
        savedTask.setStudent(student);
        savedTask.setStatus(TaskStatus.PENDING);

        when(studentRepo.findByStudentId(1001L)).thenReturn(Optional.of(student));
        when(taskRepo.save(any(Task.class))).thenReturn(savedTask);

        // when
        CreateTaskResponse response = taskService.createTask(request);

        // then — status must always be PENDING on creation
        assertThat(response.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void createTask_shouldWorkWithoutDueDate_whenDueDateIsNull() {

        // given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Study Spring Boot");
        request.setStudentId(1001L);
        request.setDueDate(null);  // no due date — should be allowed

        Student student = new Student();
        student.setStudentId(1001L);

        Task savedTask = new Task();
        savedTask.setTaskId(55123456L);
        savedTask.setTitle("Study Spring Boot");
        savedTask.setStudent(student);
        savedTask.setStatus(TaskStatus.PENDING);

        when(studentRepo.findByStudentId(1001L)).thenReturn(Optional.of(student));
        when(taskRepo.save(any(Task.class))).thenReturn(savedTask);

        // when
        CreateTaskResponse response = taskService.createTask(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Study Spring Boot");
    }

    @Test
    void getAllTasks_shouldReturnAllTasks_whenNoFilters() {

        // given
        Student student = new Student();
        student.setStudentId(1001L);

        Task task1 = new Task();
        task1.setTaskId(1L);
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.PENDING);
        task1.setStudent(student);

        Task task2 = new Task();
        task2.setTaskId(2L);
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setStudent(student);

        when(taskRepo.findAll()).thenReturn(List.of(task1, task2));

        // when
        List<CreateTaskResponse> responses = taskService.getAllTasks(null, null);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("Task 1");
        assertThat(responses.get(1).getTitle()).isEqualTo("Task 2");
    }

    @Test
    void getAllTasks_shouldReturnFilteredTasks_whenStudentIdProvided() {

        // given
        Student student = new Student();
        student.setStudentId(1001L);

        Task task1 = new Task();
        task1.setTaskId(1L);
        task1.setTitle("Task 1");
        task1.setStudent(student);
        task1.setStatus(TaskStatus.PENDING);

        when(studentRepo.existsById(1001L)).thenReturn(true);
        when(taskRepo.findByStudentStudentId(1001L)).thenReturn(List.of(task1));

        // when
        List<CreateTaskResponse> responses = taskService.getAllTasks(1001L, null);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStudentId()).isEqualTo(1001L);
    }

    @Test
    void getAllTasks_shouldReturnFilteredTasks_whenStatusProvided() {

        // given
        Student student = new Student();
        student.setStudentId(1001L);

        Task task1 = new Task();
        task1.setTaskId(1L);
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.PENDING);
        task1.setStudent(student);

        when(taskRepo.findByStatus(TaskStatus.PENDING)).thenReturn(List.of(task1));

        // when
        List<CreateTaskResponse> responses = taskService.getAllTasks(null, TaskStatus.PENDING);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void getAllTasks_shouldReturnFilteredTasks_whenBothFiltersProvided() {

        // given
        Student student = new Student();
        student.setStudentId(1001L);

        Task task1 = new Task();
        task1.setTaskId(1L);
        task1.setTitle("Task 1");
        task1.setStudent(student);
        task1.setStatus(TaskStatus.PENDING);

        when(studentRepo.existsById(1001L)).thenReturn(true);
        when(taskRepo.findByStudentStudentIdAndStatus(1001L, TaskStatus.PENDING))
                .thenReturn(List.of(task1));

        // when
        List<CreateTaskResponse> responses = taskService.getAllTasks(1001L, TaskStatus.PENDING);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStudentId()).isEqualTo(1001L);
        assertThat(responses.get(0).getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void getAllTasks_shouldReturnEmptyList_whenNoTasksExist() {

        // given
        when(taskRepo.findAll()).thenReturn(List.of());

        // when
        List<CreateTaskResponse> responses = taskService.getAllTasks(null, null);

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    void getAllTasks_shouldThrowResourceNotFound_whenStudentDoesNotExist() {

        // given
        when(studentRepo.existsById(999L)).thenReturn(false);

        // when + then
        assertThatThrownBy(() -> taskService.getAllTasks(999L, null))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");
    }


    // ─────────────────────────────────────────────────────────────
    // GET TASK BY ID
    // ─────────────────────────────────────────────────────────────

    @Test
    void getTaskById_shouldReturnTask_whenTaskExists() {

        // given
        Student student = new Student();
        student.setStudentId(1001L);

        Task task = new Task();
        task.setTaskId(55123456L);
        task.setTitle("Study Spring Boot");
        task.setStudent(student);
        task.setStatus(TaskStatus.PENDING);

        when(taskRepo.findByTaskId(anyLong())).thenReturn(Optional.of(task));

        // when
        CreateTaskResponse response = taskService.getTaskById(55123456L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Study Spring Boot");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void getTaskById_shouldThrowResourceNotFound_whenTaskDoesNotExist() {

        // given
        when(taskRepo.findByTaskId(anyLong())).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");
    }


    // ─────────────────────────────────────────────────────────────
    // DELETE TASK
    // ─────────────────────────────────────────────────────────────

    @Test
    void deleteTask_shouldReturnSuccessMessage_whenTaskExists() {

        // given
        Task task = new Task();
        task.setTaskId(55123456L);
        task.setTitle("Study Spring Boot");

        when(taskRepo.findByTaskId(anyLong())).thenReturn(Optional.of(task));

        // when
        String result = taskService.deleteTask(55123456L);

        // then
        assertThat(result).contains("deleted successfully");
        verify(taskRepo, times(1)).delete(task);
    }

    @Test
    void deleteTask_shouldThrowResourceNotFound_whenTaskDoesNotExist() {

        // given
        when(taskRepo.findByTaskId(anyLong())).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");

        // verify delete was never called
        verify(taskRepo, never()).delete(any());
    }


    // ─────────────────────────────────────────────────────────────
    // UPDATE TASK
    // ─────────────────────────────────────────────────────────────

    @Test
    void updateTask_shouldReturnUpdatedResponse_whenValidRequest() {

        // given
        Student student = new Student();
        student.setStudentId(1001L);

        Task existingTask = new Task();
        existingTask.setTaskId(55123456L);
        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskStatus.PENDING);
        existingTask.setStudent(student);

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("New Title");
        request.setStatus(TaskStatus.COMPLETED);

        Task updatedTask = new Task();
        updatedTask.setTaskId(55123456L);
        updatedTask.setTitle("New Title");
        updatedTask.setDescription("Old Description");  // unchanged
        updatedTask.setStatus(TaskStatus.COMPLETED);
        updatedTask.setStudent(student);

        when(taskRepo.findByTaskId(anyLong())).thenReturn(Optional.of(existingTask));
        when(taskRepo.save(any(Task.class))).thenReturn(updatedTask);

        // when
        CreateTaskResponse response = taskService.updateTask(55123456L, request);

        // then
        assertThat(response.getTitle()).isEqualTo("New Title");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(response.getStudentId()).isEqualTo(1001L);
    }

    @Test
    void updateTask_shouldOnlyUpdateProvidedFields_whenPartialRequest() {

        // given
        Student student = new Student();
        student.setStudentId(1001L);

        Task existingTask = new Task();
        existingTask.setTaskId(55123456L);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(TaskStatus.PENDING);
        existingTask.setStudent(student);

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setStatus(TaskStatus.COMPLETED);  // only update status
        // title and description are null — should stay unchanged

        Task savedTask = new Task();
        savedTask.setTaskId(55123456L);
        savedTask.setTitle("Original Title");      // unchanged
        savedTask.setDescription("Original Description"); // unchanged
        savedTask.setStatus(TaskStatus.COMPLETED); // updated
        savedTask.setStudent(student);

        when(taskRepo.findByTaskId(anyLong())).thenReturn(Optional.of(existingTask));
        when(taskRepo.save(any(Task.class))).thenReturn(savedTask);

        // when
        CreateTaskResponse response = taskService.updateTask(55123456L, request);

        // then — title and description should NOT change
        assertThat(response.getTitle()).isEqualTo("Original Title");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    void updateTask_shouldThrowResourceNotFound_whenTaskDoesNotExist() {

        // given
        when(taskRepo.findByTaskId(anyLong())).thenReturn(Optional.empty());

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("New Title");

        // when + then
        assertThatThrownBy(() -> taskService.updateTask(999L, request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("not found");

        // verify save was never called
        verify(taskRepo, never()).save(any());
    }

    @Test
    void updateTask_shouldNotChangeAnything_whenAllFieldsAreNull() {

        // given
        Student student = new Student();
        student.setStudentId(1001L);

        Task existingTask = new Task();
        existingTask.setTaskId(55123456L);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(TaskStatus.PENDING);
        existingTask.setStudent(student);

        UpdateTaskRequest request = new UpdateTaskRequest();
        // all fields null — nothing should change

        when(taskRepo.findByTaskId(anyLong())).thenReturn(Optional.of(existingTask));
        when(taskRepo.save(any(Task.class))).thenReturn(existingTask);

        // when
        CreateTaskResponse response = taskService.updateTask(55123456L, request);

        // then — everything unchanged
        assertThat(response.getTitle()).isEqualTo("Original Title");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.PENDING);
    }
}
