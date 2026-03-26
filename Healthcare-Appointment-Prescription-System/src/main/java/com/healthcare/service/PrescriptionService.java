package com.healthcare.service;

import com.healthcare.dto.CreatePrescriptionRequest;
import com.healthcare.dto.PrescriptionResponse;
import com.healthcare.entity.Prescription;

import java.util.List;

public interface PrescriptionService {
    Prescription createPrescription(CreatePrescriptionRequest request);
    List<PrescriptionResponse> getPrescriptionsByPatient(Long patientId);
    List<PrescriptionResponse> getPrescriptionsByDoctor(Long doctorId);
    List<PrescriptionResponse> getAllPrescriptions();
    byte[] downloadPrescriptionPdf(Long prescriptionId);

    // ✅ ADD THIS
    void sendPrescriptionEmail(Long prescriptionId);
}