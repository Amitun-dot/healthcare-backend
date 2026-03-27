package com.healthcare.controller;

import com.healthcare.dto.CreatePrescriptionRequest;
import com.healthcare.dto.PrescriptionResponse;
import com.healthcare.entity.Prescription;
import com.healthcare.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public Prescription createPrescription(@Valid @RequestBody CreatePrescriptionRequest request) {
        return prescriptionService.createPrescription(request);
    }

    @GetMapping("/patient/{patientId}")
    public List<PrescriptionResponse> getPrescriptionsByPatient(@PathVariable Long patientId) {
        return prescriptionService.getPrescriptionsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<PrescriptionResponse> getPrescriptionsByDoctor(@PathVariable Long doctorId) {
        return prescriptionService.getPrescriptionsByDoctor(doctorId);
    }

    @GetMapping("/{prescriptionId}/download")
    public ResponseEntity<byte[]> downloadPrescriptionPdf(@PathVariable Long prescriptionId) {
        byte[] pdfBytes = prescriptionService.downloadPrescriptionPdf(prescriptionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=prescription_" + prescriptionId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/{prescriptionId}/send-email")
    public ResponseEntity<String> sendPrescriptionEmail(@PathVariable Long prescriptionId) {
        prescriptionService.sendPrescriptionEmail(prescriptionId);
        return ResponseEntity.ok("Prescription email sent successfully");
    }
}