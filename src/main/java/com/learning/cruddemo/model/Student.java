package com.learning.cruddemo.model;

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
@EntityListeners(AuditingEntityListener.class)// TODO: is this required? EntityListeners
@Table(name="student")
@Builder
public class Student {

    @Id
    @Column(updatable = false, nullable = false)
    private long studentId; // TODO : we generally use long and

    private String studentName;

    @Column(unique = true)
    private long rollNumber;

    @Column(unique = true)
    private String email;

    @CreatedDate
    private LocalDate createdOn;

    @LastModifiedDate
    private LocalDate lastUpdatedOn;
}
