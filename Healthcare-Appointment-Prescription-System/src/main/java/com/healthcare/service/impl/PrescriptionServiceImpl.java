package com.healthcare.service.impl;

import com.healthcare.dto.CreatePrescriptionRequest;
import com.healthcare.dto.PrescriptionResponse;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.AppointmentStatus;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.entity.Prescription;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.service.EmailService;
import com.healthcare.service.PdfService;
import com.healthcare.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PdfService pdfService;
    private final EmailService emailService;

    @Override
    public Prescription createPrescription(CreatePrescriptionRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("This appointment does not belong to this doctor");
        }

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("This appointment does not belong to this patient");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot create prescription for cancelled appointment");
        }

        if (prescriptionRepository.findByAppointment(appointment).isPresent()) {
            throw new RuntimeException("Prescription already exists for this appointment");
        }

        Prescription prescription = Prescription.builder()
                .appointment(appointment)
                .doctor(doctor)
                .patient(patient)
                .diagnosis(request.getDiagnosis())
                .medicines(request.getMedicines())
                .notes(request.getNotes())
                .build();

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        try {
            String patientEmail = savedPrescription.getPatient().getUser().getEmail();
            String patientName = savedPrescription.getPatient().getUser().getName();

            if (patientEmail != null && !patientEmail.isBlank()) {
                byte[] pdfBytes = pdfService.generatePrescriptionPdf(savedPrescription);
                emailService.sendPrescriptionEmail(patientEmail, patientName, pdfBytes);
            }
        } catch (Exception e) {
            System.out.println("Prescription saved, but email sending failed: " + e.getMessage());
        }

        return savedPrescription;
    }

    @Override
    public List<PrescriptionResponse> getPrescriptionsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return prescriptionRepository.findByPatient(patient)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PrescriptionResponse> getPrescriptionsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return prescriptionRepository.findByDoctor(doctor)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public byte[] downloadPrescriptionPdf(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        return pdfService.generatePrescriptionPdf(prescription);
    }

    @Override
    public void sendPrescriptionEmail(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        String patientEmail = prescription.getPatient().getUser().getEmail();
        String patientName = prescription.getPatient().getUser().getName();

        if (patientEmail == null || patientEmail.isBlank()) {
            throw new RuntimeException("Patient email not available");
        }

        byte[] pdfBytes = pdfService.generatePrescriptionPdf(prescription);
        emailService.sendPrescriptionEmail(patientEmail, patientName, pdfBytes);
    }

    private PrescriptionResponse mapToResponse(Prescription prescription) {
        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .diagnosis(prescription.getDiagnosis())
                .medicines(prescription.getMedicines())
                .notes(prescription.getNotes())
                .appointmentId(prescription.getAppointment().getId())
                .appointmentDate(prescription.getAppointment().getAppointmentDate().toString())
                .appointmentTime(prescription.getAppointment().getAppointmentTime().toString())
                .status(prescription.getAppointment().getStatus().name())
                .doctorName(prescription.getDoctor().getUser().getName())
                .patientName(prescription.getPatient().getUser().getName())
                .build();
    }
}