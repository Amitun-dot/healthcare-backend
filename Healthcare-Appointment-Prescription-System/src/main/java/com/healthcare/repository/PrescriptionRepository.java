package com.healthcare.repository;

import com.healthcare.entity.Appointment;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatient(Patient patient);

    List<Prescription> findByDoctor(Doctor doctor);

    Optional<Prescription> findByAppointment(Appointment appointment);

    // ✅ NEW: required for admin delete flow
    void deleteByDoctor(Doctor doctor);

    void deleteByPatient(Patient patient);

    void deleteByAppointment(Appointment appointment);
}