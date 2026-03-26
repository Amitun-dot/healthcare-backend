package com.healthcare.service;

import com.healthcare.entity.Prescription;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generatePrescriptionPdf(Prescription prescription) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);

            document.open();

            Color primary = new Color(33, 150, 243);
            Color dark = new Color(33, 37, 41);
            Color lightBg = new Color(245, 249, 252);
            Color border = new Color(220, 226, 232);
            Color muted = new Color(108, 117, 125);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.WHITE);
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, dark);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, dark);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, dark);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, muted);

            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(primary);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setPadding(16f);

            Paragraph title = new Paragraph("Modern Healthcare Prescription", titleFont);
            title.setAlignment(Element.ALIGN_LEFT);

            Paragraph subtitle = new Paragraph("Digital Prescription Report", subTitleFont);
            subtitle.setSpacingBefore(4f);

            headerCell.addElement(title);
            headerCell.addElement(subtitle);
            headerTable.addCell(headerCell);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            Paragraph idPara = new Paragraph("Prescription ID: " + prescription.getId(), valueFont);
            idPara.setSpacingAfter(12f);
            document.add(idPara);

            document.add(createSectionBox(
                    "Patient Details",
                    new String[][]{
                            {"Name", safe(prescription.getPatient().getUser().getName())},
                            {"Phone", safe(prescription.getPatient().getPhone())},
                            {"Gender", safe(String.valueOf(prescription.getPatient().getGender()))},
                            {"Age", safe(String.valueOf(prescription.getPatient().getAge()))},
                            {"Address", safe(prescription.getPatient().getAddress())}
                    },
                    sectionFont, labelFont, valueFont, lightBg, border
            ));

            document.add(Chunk.NEWLINE);

            document.add(createSectionBox(
                    "Doctor Details",
                    new String[][]{
                            {"Doctor Name", safe(prescription.getDoctor().getUser().getName())}
                    },
                    sectionFont, labelFont, valueFont, lightBg, border
            ));

            document.add(Chunk.NEWLINE);

            document.add(createSectionBox(
                    "Appointment Details",
                    new String[][]{
                            {"Appointment ID", safe(String.valueOf(prescription.getAppointment().getId()))},
                            {"Date", safe(String.valueOf(prescription.getAppointment().getAppointmentDate()))},
                            {"Time", safe(String.valueOf(prescription.getAppointment().getAppointmentTime()))},
                            {"Status", safe(String.valueOf(prescription.getAppointment().getStatus()))}
                    },
                    sectionFont, labelFont, valueFont, lightBg, border
            ));

            document.add(Chunk.NEWLINE);

            document.add(createSectionBox(
                    "Prescription Details",
                    new String[][]{
                            {"Diagnosis", valueOrNA(prescription.getDiagnosis())},
                            {"Medicines", valueOrNA(prescription.getMedicines())},
                            {"Notes", valueOrNA(prescription.getNotes())}
                    },
                    sectionFont, labelFont, valueFont, lightBg, border
            ));

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            Paragraph footer = new Paragraph("Get well soon. Wishing you a healthy recovery.", footerFont);
            footer.setAlignment(Element.ALIGN_LEFT);
            document.add(footer);

            Paragraph sign = new Paragraph("Authorized by Modern Healthcare System", footerFont);
            sign.setAlignment(Element.ALIGN_RIGHT);
            sign.setSpacingBefore(20f);
            document.add(sign);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate prescription PDF", e);
        }
    }

    private PdfPTable createSectionBox(
            String title,
            String[][] rows,
            Font sectionFont,
            Font labelFont,
            Font valueFont,
            Color bgColor,
            Color borderColor
    ) throws DocumentException {

        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100);

        PdfPCell outerCell = new PdfPCell();
        outerCell.setBackgroundColor(bgColor);
        outerCell.setBorderColor(borderColor);
        outerCell.setPadding(12f);

        Paragraph sectionTitle = new Paragraph(title, sectionFont);
        sectionTitle.setSpacingAfter(10f);
        outerCell.addElement(sectionTitle);

        PdfPTable innerTable = new PdfPTable(2);
        innerTable.setWidthPercentage(100);
        innerTable.setWidths(new float[]{2f, 5f});

        for (String[] row : rows) {
            PdfPCell labelCell = new PdfPCell(new Phrase(row[0], labelFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setPadding(6f);
            labelCell.setVerticalAlignment(Element.ALIGN_TOP);

            PdfPCell valueCell = new PdfPCell(new Phrase(row[1], valueFont));
            valueCell.setBorder(Rectangle.NO_BORDER);
            valueCell.setPadding(6f);
            valueCell.setVerticalAlignment(Element.ALIGN_TOP);

            innerTable.addCell(labelCell);
            innerTable.addCell(valueCell);
        }

        outerCell.addElement(innerTable);
        outerTable.addCell(outerCell);

        return outerTable;
    }

    private String valueOrNA(String value) {
        return (value != null && !value.isBlank()) ? value : "N/A";
    }

    private String safe(String value) {
        return (value != null && !value.isBlank()) ? value : "N/A";
    }
}