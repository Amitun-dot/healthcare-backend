package com.healthcare.service;

import com.healthcare.dto.CreateDoctorRequest;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Role;
import com.healthcare.entity.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public String createDoctor(CreateDoctorRequest request) {

        // check email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // create USER
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.DOCTOR)
                .build();

        userRepository.save(user);

        // create DOCTOR
        Doctor doctor = Doctor.builder()
                .specialization(request.getSpecialization())
                .phone(request.getPhone())
                .experienceYears(request.getExperienceYears())
                .user(user)
                .build();

        doctorRepository.save(doctor);

        return "Doctor created successfully";
    }
}