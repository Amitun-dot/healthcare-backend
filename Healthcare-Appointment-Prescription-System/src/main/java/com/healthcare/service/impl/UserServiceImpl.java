package com.healthcare.service.impl;

import com.healthcare.dto.UserResponse;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.entity.Role;
import com.healthcare.entity.User;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole().name()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.PATIENT) {
            Patient patient = patientRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Patient profile not found"));

            // 1. delete prescriptions linked to patient
            prescriptionRepository.deleteAll(prescriptionRepository.findByPatient(patient));

            // 2. delete appointments linked to patient
            appointmentRepository.deleteAll(appointmentRepository.findByPatient(patient));

            // 3. delete patient profile
            patientRepository.delete(patient);
        }

        if (user.getRole() == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

            // 1. delete prescriptions linked to doctor
            prescriptionRepository.deleteAll(prescriptionRepository.findByDoctor(doctor));

            // 2. delete appointments linked to doctor
            appointmentRepository.deleteAll(appointmentRepository.findByDoctor(doctor));

            // 3. delete doctor profile
            doctorRepository.delete(doctor);
        }

        // 4. delete user
        userRepository.delete(user);
    }
}