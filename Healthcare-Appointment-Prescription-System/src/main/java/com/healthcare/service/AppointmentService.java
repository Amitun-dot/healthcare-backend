package com.healthcare.service;

import com.healthcare.dto.AppointmentResponse;
import com.healthcare.dto.BookAppointmentRequest;
import com.healthcare.dto.UpdateAppointmentStatusRequest;
import com.healthcare.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    Appointment bookAppointment(BookAppointmentRequest request);
    List<AppointmentResponse> getAppointmentsByPatient(Long patientId);
    List<AppointmentResponse> getAppointmentsByDoctor(Long doctorId);
    Appointment updateAppointmentStatus(Long appointmentId, UpdateAppointmentStatusRequest request);
    List<AppointmentResponse> getAllAppointments();
    void cancelAppointment(Long appointmentId);
    void deleteAppointment(Long appointmentId);
}