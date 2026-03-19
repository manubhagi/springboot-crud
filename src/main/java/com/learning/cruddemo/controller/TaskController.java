package com.learning.cruddemo.controller;

import com.learning.cruddemo.dto.*;
import com.learning.cruddemo.exceptions.ApiException;
import com.learning.cruddemo.exceptions.ErrorCode;
import com.learning.cruddemo.model.TaskStatus;
import com.learning.cruddemo.service.StudentService;
import com.learning.cruddemo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {


    private final TaskService taskService;
    private final StudentService studentService;

    @PostMapping("/student")
    public CreateStudentResponse createStudent(@RequestBody CreateStudentRequest studentRequest){
        return studentService.createStudent(studentRequest);
    }

    @PostMapping("/task")
    public CreateTaskResponse createTask(@RequestBody CreateTaskRequest taskRequest){

        return taskService.createTask(taskRequest);
    }



    @GetMapping("/task")
    public List<CreateTaskResponse> getAllTasks(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String status) {

        TaskStatus taskStatus = null;
        if (status != null) {
            try {
                taskStatus = TaskStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw ApiException.badRequest(ErrorCode.INVALID_STATUS,
                        "Invalid status: " + status + ". Allowed values: PENDING, COMPLETED, EXPIRED");
            }
        }

        return taskService.getAllTasks(studentId, taskStatus);
    }
    // TODO :  take status as string, convert to enum, if enum does not match, throw 400  (done)

    @GetMapping("/task/{id}")
    public CreateTaskResponse getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }


    @GetMapping("/student/{id}")
    public CreateStudentResponse getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }



    @DeleteMapping("/task/{id}")
    public String deleteTaskById(@PathVariable Long id){
        return taskService.deleteTask(id);
    }

    @PatchMapping("/task/{id}")
    public CreateTaskResponse updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest request){
        return taskService.updateTask(id,request);
    }

}
