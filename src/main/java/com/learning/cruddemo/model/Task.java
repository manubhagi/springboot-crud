package com.learning.cruddemo.model;

import com.learning.cruddemo.model.Student;
import com.learning.cruddemo.model.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class) // TODO: is this required? EntityListeners //used for createddate and lastmodifieddate (can use hibernate like @creationtimestamp)
@Table(name="task") // TODO: either keep task, student or keep tasks, students or student, tasks - search
public class Task {
    @Id
    @Column(updatable = false, nullable = false)
    private long taskId; // TODO:  generate UUID
    private String title;
    private String description;
    private LocalDate dueDate;
    @CreatedDate
    private LocalDate createdOn;
    @LastModifiedDate
    private LocalDate lastUpdatedOn;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
