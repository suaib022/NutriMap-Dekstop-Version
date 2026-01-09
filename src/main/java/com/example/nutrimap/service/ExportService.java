package com.example.nutrimap.service;

import com.example.nutrimap.model.ChildModel;
import com.example.nutrimap.model.VisitModel;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for exporting data to CSV and PDF formats.
 */
public class ExportService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // ==================== CSV EXPORT ====================
    
    /**
     * Export children data to CSV file.
     */
    public static void exportChildrenToCsv(List<ChildModel> children, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Header
            writer.println("ID,Full Name,Father's Name,Mother's Name,Contact,Gender,Date of Birth,Division,District,Upazila,Union,Branch,Last Visit");
            
            // Data rows
            for (ChildModel child : children) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    child.getId(),
                    escapeCsv(child.getFullName()),
                    escapeCsv(child.getFathersName()),
                    escapeCsv(child.getMothersName()),
                    escapeCsv(child.getContactNumber()),
                    escapeCsv(child.getGender()),
                    escapeCsv(child.getDateOfBirth()),
                    escapeCsv(child.getDivision()),
                    escapeCsv(child.getDistrict()),
                    escapeCsv(child.getUpazilla()),
                    escapeCsv(child.getUnionName()),
                    escapeCsv(child.getBranchName()),
                    escapeCsv(child.getLastVisit())
                );
            }
        }
    }
    
    /**
     * Export visits data to CSV file.
     */
    public static void exportVisitsToCsv(List<VisitModel> visits, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Header
            writer.println("Visit ID,Child Name,Visit Date,Weight (kg),Height (cm),MUAC (mm),Risk Level,Notes");
            
            // Data rows
            for (VisitModel visit : visits) {
                writer.printf("%d,%s,%s,%.2f,%.2f,%d,%s,%s%n",
                    visit.getVisitId(),
                    escapeCsv(visit.getChildName()),
                    escapeCsv(visit.getVisitDate()),
                    visit.getWeightKg(),
                    visit.getHeightCm(),
                    visit.getMuacMm(),
                    escapeCsv(visit.getRiskLevel()),
                    escapeCsv(visit.getNotes())
                );
            }
        }
    }
    
    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    // ==================== PDF EXPORT ====================
    
    /**
     * Export children data to PDF file.
     */
    public static void exportChildrenToPdf(List<ChildModel> children, File file) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        
        // Title
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Children Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);
        
        // Timestamp
        Font dateFont = new Font(Font.HELVETICA, 10, Font.ITALIC);
        Paragraph date = new Paragraph("Generated: " + LocalDateTime.now().format(DATE_FORMATTER), dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20);
        document.add(date);
        
        // Table
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 2f, 1.5f, 2f, 1.5f, 1.5f});
        
        // Header
        addTableHeader(table, "ID", "Full Name", "Father's Name", "Gender", "District", "DOB", "Risk");
        
        // Data rows
        for (ChildModel child : children) {
            addTableCell(table, String.valueOf(child.getId()));
            addTableCell(table, child.getFullName());
            addTableCell(table, child.getFathersName());
            addTableCell(table, child.getGender());
            addTableCell(table, child.getDistrict());
            addTableCell(table, child.getDateOfBirth());
            addTableCell(table, child.getDisplayLastVisit());
        }
        
        document.add(table);
        document.close();
    }
    
    /**
     * Export visits data to PDF file.
     */
    public static void exportVisitsToPdf(List<VisitModel> visits, File file) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        
        // Title
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Visits Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);
        
        // Timestamp
        Font dateFont = new Font(Font.HELVETICA, 10, Font.ITALIC);
        Paragraph date = new Paragraph("Generated: " + LocalDateTime.now().format(DATE_FORMATTER), dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20);
        document.add(date);
        
        // Table
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 1.5f, 1f, 1f, 1f, 1.5f});
        
        // Header
        addTableHeader(table, "ID", "Child Name", "Visit Date", "Weight", "Height", "MUAC", "Risk");
        
        // Data rows
        for (VisitModel visit : visits) {
            addTableCell(table, String.valueOf(visit.getVisitId()));
            addTableCell(table, visit.getChildName());
            addTableCell(table, visit.getVisitDate());
            addTableCell(table, String.format("%.1f kg", visit.getWeightKg()));
            addTableCell(table, String.format("%.1f cm", visit.getHeightCm()));
            addTableCell(table, visit.getMuacMm() + " mm");
            addRiskCell(table, visit.getRiskLevel());
        }
        
        document.add(table);
        document.close();
    }
    
    private static void addTableHeader(PdfPTable table, String... headers) {
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.BLACK);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
    
    private static void addTableCell(PdfPTable table, String value) {
        Font cellFont = new Font(Font.HELVETICA, 9);
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value : "", cellFont));
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    private static void addRiskCell(PdfPTable table, String riskLevel) {
        Font cellFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(riskLevel != null ? riskLevel : "N/A", cellFont));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        // Set background color based on risk level
        if ("High".equalsIgnoreCase(riskLevel)) {
            cell.setBackgroundColor(new Color(231, 76, 60)); // Red
        } else if ("Medium".equalsIgnoreCase(riskLevel)) {
            cell.setBackgroundColor(new Color(243, 156, 18)); // Orange
        } else if ("Low".equalsIgnoreCase(riskLevel)) {
            cell.setBackgroundColor(new Color(39, 174, 96)); // Green
        } else {
            cell.setBackgroundColor(new Color(149, 165, 166)); // Gray
            cellFont = new Font(Font.HELVETICA, 9);
            cell.setPhrase(new Phrase(riskLevel != null ? riskLevel : "N/A", cellFont));
        }
        
        table.addCell(cell);
    }
}
