package com.healthcare.repository;

import com.healthcare.entity.Patient;
import com.healthcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // ✅ FIXED: correct mapping
    Optional<Patient> findByUser(User user);
}