package com.learning.cruddemo.repository;

import com.learning.cruddemo.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;


@Repository
public interface StudentRepo extends JpaRepository<Student,Long> {
    boolean existsByEmail(String email);
    boolean existsByRollNumber(long rollNumber);

    Optional<Student> findByStudentId(Long studentId);
}
