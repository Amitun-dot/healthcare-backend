package com.healthcare.repository;

import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentStatus;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDoctor(Doctor doctor);

    // ✅ NEW: check if slot already booked (excluding cancelled)
    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Doctor doctor,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus status
    );
}