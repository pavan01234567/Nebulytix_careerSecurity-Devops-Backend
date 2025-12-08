package com.neb.util;

import java.io.ByteArrayOutputStream;
import java.io.File;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.neb.entity.Employee;
import com.neb.entity.Payslip;

public class PdfGeneratorUtil {

    public static byte[] createPayslipPdf(Employee emp, Payslip p) throws Exception {
        // PDF output stream setup
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // ---------------------------------------------------------
        // Add company logo (with file check)
        // ---------------------------------------------------------
        String logoPath = "E:/NEBULYTIX TECHNOLOGIES/files/nebTechLogo.jpg";
       // String stampPath = "E:/NEBULYTIX TECHNOLOGIES/files/nebulytixStamp.jpg";
       String stampPath= "E:/NEBULYTIX TECHNOLOGIES/files/nebulytixStamp.jpg";

        try {
            File file = new File(logoPath);
            if (file.exists()) {
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(120f, 120f);
                logo.setAlignment(Element.ALIGN_RIGHT);
                document.add(logo);
            } else {
                document.add(new Paragraph("NEBULYTIX TECHNOLOGIES PVT LTD",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            }
        } catch (Exception e) {
            document.add(new Paragraph("NEBULYTIX TECHNOLOGIES PVT LTD",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        }

        // Fonts
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        // Header details
        document.add(new Paragraph("Payslip for the month: " + p.getPayslipMonth(), boldFont));
        document.add(new Paragraph("Employee: " + emp.getFirstName() + " " + emp.getLastName()
                + "   |   Card No: " + emp.getCardNumber(), normalFont));
        document.add(new Paragraph("\n"));

        // ========================= Table 1 – Employee Info =========================
        PdfPTable table1 = new PdfPTable(2);
        table1.setWidthPercentage(100f);
        // ... (same table1 additions as your original code) ...
        table1.addCell(createCellOuterColumnBorders("Location: " + p.getLocation(), normalFont, true, true, true, false));
//        table1.addCell(createCellOuterColumnBorders("P.F.No: " + emp.getPfNumber(), normalFont, true, false, true, false));
//        table1.addCell(createCellOuterColumnBorders("Bank A/C No: " + emp.getBankAccountNumber() 
//                + "   Bank: " + emp.getBankName(), normalFont, false, true, true, false));
//        table1.addCell(createCellOuterColumnBorders("E.P.S No: " + emp.getEpsNumber(), normalFont, false, false, true, false));
//        table1.addCell(createCellOuterColumnBorders("No. of days paid: " + emp.getDaysPresent(), normalFont, false, true, true, false));
//        table1.addCell(createCellOuterColumnBorders("PAN: " + emp.getPanNumber(), normalFont, false, false, true, false));
//        table1.addCell(createCellOuterColumnBorders("", normalFont, false, true, true, false));
//        table1.addCell(createCellOuterColumnBorders("UAN: " + emp.getUanNumber(), normalFont, false, false, true, false));
//        table1.addCell(createCellOuterColumnBorders("", normalFont, false, true, true, false));
//        table1.addCell(createCellOuterColumnBorders("ESI No.: " + emp.getEsiNumber(), normalFont, false, false, true, false));
        table1.addCell(createCellOuterColumnBorders("", normalFont, false, true, true, false));
        table1.addCell(createCellOuterColumnBorders("DOJ: " + emp.getJoiningDate(), normalFont, false, false, true, true));
        document.add(table1);
        document.add(new Paragraph("\n"));

        // ========================= Table 2 – Earnings / Deductions =========================
        PdfPTable table2 = new PdfPTable(3);
        table2.setWidthPercentage(100f);
        // ... (same table2 additions as your original code) ...
        table2.addCell(createCellOuterColumnBorders("Earnings", boldFont, true, true, true, true));
        table2.addCell(createCellOuterColumnBorders("Statutory Deductions", boldFont, true, false, true, true));
        table2.addCell(createCellOuterColumnBorders("Scheme Deductions", boldFont, true, false, true, true));
        table2.addCell(createCellOuterColumnBorders("Basic: " + p.getBasic(), normalFont, false, true, true, false));
        table2.addCell(createCellOuterColumnBorders("PF: " + p.getPfDeduction(), normalFont, false, false, true, false));
        table2.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table2.addCell(createCellOuterColumnBorders("HRA: " + p.getHra(), normalFont, false, true, true, false));
        table2.addCell(createCellOuterColumnBorders("PROFTAX: " + p.getProfTaxDeduction(), normalFont, false, false, true, false));
        table2.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table2.addCell(createCellOuterColumnBorders("Flexi: " + p.getFlexi(), normalFont, false, true, true, false));
        table2.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table2.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table2.addCell(createCellOuterColumnBorders("Earnings (Total): " + p.getGrossSalary(), normalFont, true, true, true, true));
        table2.addCell(createCellOuterColumnBorders("Deductions (Total): " + p.getTotalDeductions(), normalFont, true, false, true, true));
        table2.addCell(createCellOuterColumnBorders("Net Pay: " + p.getNetSalary(), normalFont, true, false, true, true));
        document.add(table2);
        document.add(new Paragraph("\n"));

        // ========================= Table 3 – Tax / Perks =========================
        PdfPTable table3 = new PdfPTable(4);
        table3.setWidthPercentage(100f);
        // ... (same table3 additions as your original code) ...
        table3.addCell(createCellOuterColumnBorders("Perk Details", boldFont, true, true, true, true));
        table3.addCell(createCellOuterColumnBorders("Any other Income", boldFont, true, false, true, true));
        table3.addCell(createCellOuterColumnBorders("Annual exemption", boldFont, true, false, true, true));
        table3.addCell(createCellOuterColumnBorders("Form 16 Summary", boldFont, true, false, true, true));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, true, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("Gross Salary: " + p.getGrossSalary(), normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, true, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("Balance: " + p.getBalance(), normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, true, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("Agg Deduction: " + p.getAggrgDeduction(), normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, true, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("Income under Hd Salary: " + p.getIncHdSalary(), normalFont, false, false, true, false));
        table3.addCell(createCellOuterColumnBorders("", normalFont, true, true, true, true));
        table3.addCell(createCellOuterColumnBorders("", normalFont, true, false, true, true));
        table3.addCell(createCellOuterColumnBorders("", normalFont, true, false, true, true));
        table3.addCell(createCellOuterColumnBorders("Tax credit: " + p.getTaxCredit(), normalFont, true, false, true, true));
        document.add(table3);
        document.add(new Paragraph("\n"));

        // Footer note
        document.add(new Paragraph("This is a computer-generated document and does not require a signature.", normalFont));

        // --- Add stamp image at bottom right of page ---
        try {
            Image stamp = Image.getInstance(stampPath);
            stamp.scaleToFit(100f, 100f);  // adjust size as needed
            Rectangle pageSize = document.getPageSize();
            float x = pageSize.getRight() - stamp.getScaledWidth() - document.rightMargin();
            float y = pageSize.getBottom() + document.bottomMargin();
            stamp.setAbsolutePosition(x, y);
            PdfContentByte canvas = writer.getDirectContent();
            canvas.addImage(stamp);
        } catch (Exception e) {
            // log or ignore — stamp addition failure won't break PDF
            System.err.println("Unable to add stamp image: " + e.getMessage());
        }

        document.close();
        return baos.toByteArray();
    }

    // Helper: create table cell with custom borders
    private static PdfPCell createCellOuterColumnBorders(String text, Font font,
                                                         boolean borderTop, boolean borderLeft,
                                                         boolean borderRight, boolean borderBottom) {

        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        int border = 0;
        if (borderTop) border |= PdfPCell.TOP;
        if (borderLeft) border |= PdfPCell.LEFT;
        if (borderRight) border |= PdfPCell.RIGHT;
        if (borderBottom) border |= PdfPCell.BOTTOM;
        cell.setBorder(border);
        cell.setBorderWidth(1f);
        return cell;
    }
}
