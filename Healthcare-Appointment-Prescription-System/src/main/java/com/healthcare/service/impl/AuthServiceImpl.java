package com.healthcare.service.impl;

import com.healthcare.dto.AuthResponse;
import com.healthcare.dto.LoginRequest;
import com.healthcare.dto.RegisterRequest;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.entity.Role;
import com.healthcare.entity.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.UserRepository;
import com.healthcare.security.JwtService;
import com.healthcare.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (request.getRole() == null) {
            throw new RuntimeException("Role is required");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin registration is not allowed");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        if (request.getRole() == Role.DOCTOR) {
            Doctor doctor = Doctor.builder()
                    .specialization(request.getSpecialization())
                    .phone(request.getDoctorPhone())
                    .experienceYears(request.getExperienceYears())
                    .user(savedUser)
                    .build();

            doctorRepository.save(doctor);

        } else if (request.getRole() == Role.PATIENT) {
            Patient patient = Patient.builder()
                    .age(request.getAge())
                    .gender(request.getGender())
                    .phone(request.getPatientPhone())
                    .address(request.getAddress())
                    .user(savedUser)
                    .build();

            patientRepository.save(patient);
        }

        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                token
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                "Login successful",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}