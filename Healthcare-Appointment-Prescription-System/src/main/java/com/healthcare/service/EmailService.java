package com.healthcare.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPrescriptionEmail(String toEmail, String patientName, byte[] pdfBytes) {
        try {
            System.out.println("Sending prescription email to: " + toEmail);
            System.out.println("Patient name: " + patientName);
            System.out.println("PDF size: " + (pdfBytes != null ? pdfBytes.length : 0));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("yourmedicaree@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Your Prescription");
            helper.setText(
                    "Dear " + patientName + ",\n\n" +
                            "Your prescription has been attached in PDF format.\n\n" +
                            "Regards,\nHealthcare Team"
            );

            helper.addAttachment("prescription.pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);

            System.out.println("Prescription email sent successfully to: " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send prescription email: " + e.getMessage(), e);
        }
    }
}