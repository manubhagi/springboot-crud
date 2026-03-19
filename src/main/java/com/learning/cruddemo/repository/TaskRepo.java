package com.learning.cruddemo.repository;

import com.learning.cruddemo.model.Task;
import com.learning.cruddemo.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByStudentStudentId(Long studentId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByStudentStudentIdAndStatus(Long studentId, TaskStatus status);
    Optional<Task> findByTaskId(Long taskId);
}
