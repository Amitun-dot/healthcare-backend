package com.healthcare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String fromEmail;

    @Value("${brevo.sender.name:Healthcare Team}")
    private String fromName;

    private static final String BREVO_SEND_EMAIL_URL = "https://api.brevo.com/v3/smtp/email";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void sendPrescriptionEmail(String toEmail, String patientName, byte[] pdfBytes) {
        try {
            System.out.println("📧 Sending prescription email via Brevo API...");
            System.out.println("➡️ To: " + toEmail);
            System.out.println("👤 Patient: " + patientName);
            System.out.println("📄 PDF size: " + (pdfBytes != null ? pdfBytes.length : 0));
            System.out.println("📤 From: " + fromEmail);

            if (toEmail == null || toEmail.isBlank()) {
                throw new RuntimeException("Recipient email is missing");
            }

            if (pdfBytes == null || pdfBytes.length == 0) {
                throw new RuntimeException("Generated PDF is empty");
            }

            if (brevoApiKey == null || brevoApiKey.isBlank()) {
                throw new RuntimeException("Brevo API key is missing");
            }

            String safePatientName = (patientName == null || patientName.isBlank()) ? "Patient" : patientName;
            String encodedPdf = Base64.getEncoder().encodeToString(pdfBytes);

            String requestBody = """
                    {
                      "sender": {
                        "name": "%s",
                        "email": "%s"
                      },
                      "to": [
                        {
                          "email": "%s",
                          "name": "%s"
                        }
                      ],
                      "subject": "Your Prescription",
                      "textContent": "Dear %s,\\n\\nYour prescription has been attached in PDF format.\\n\\nRegards,\\nHealthcare Team",
                      "attachment": [
                        {
                          "name": "prescription.pdf",
                          "content": "%s"
                        }
                      ]
                    }
                    """.formatted(
                    escapeJson(fromName),
                    escapeJson(fromEmail),
                    escapeJson(toEmail),
                    escapeJson(safePatientName),
                    escapeJson(safePatientName),
                    encodedPdf
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_SEND_EMAIL_URL))
                    .timeout(Duration.ofSeconds(20))
                    .header("accept", "application/json")
                    .header("api-key", brevoApiKey.trim())
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("📬 Brevo response status: " + response.statusCode());
            System.out.println("📬 Brevo response body: " + response.body());

            if (response.statusCode() != 201) {
                throw new RuntimeException(
                        "Brevo email API failed with status " + response.statusCode() + ": " + response.body()
                );
            }

            System.out.println("✅ Email sent successfully to: " + toEmail);

        } catch (Exception e) {
            System.out.println("❌ EMAIL ERROR:");
            e.printStackTrace();
            throw new RuntimeException("Failed to send prescription email: " + e.getMessage(), e);
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}