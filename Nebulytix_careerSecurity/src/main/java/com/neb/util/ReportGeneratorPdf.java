package com.neb.util;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.neb.entity.DailyReport;
import com.neb.entity.Work;

public class ReportGeneratorPdf {

	public byte[] generateReportPDF(List<Work> works,LocalDate date) throws Exception {
		
	    if (works == null || works.isEmpty()) {
	        // optional: still create PDF with “no data” message
	    }

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    // Use landscape page size: A4.rotate()
	    Document document = new Document(PageSize.A4.rotate(), 20f, 20f, 20f, 20f);
	    PdfWriter.getInstance(document, baos);
	    document.open();

	    // Title / header row
	    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
	    Paragraph title = new Paragraph("Daily Report", titleFont);
	    title.setAlignment(Element.ALIGN_LEFT);
	    document.add(title);

	    Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
	    Paragraph datePara = new Paragraph("Submitted Date: " + date.toString(), dateFont);
	    datePara.setAlignment(Element.ALIGN_RIGHT);
	    document.add(datePara);

	    document.add(Chunk.NEWLINE);

	    // Create table with 6 columns
	    PdfPTable table = new PdfPTable(6);
	    table.setWidthPercentage(100f);
	    table.setSpacingBefore(10f);
	    table.setSpacingAfter(10f);

	    // Set column widths (you can tweak these)
	    float[] columnWidths = {2f, 3f, 3f, 4f, 2f, 6f};
	    table.setWidths(columnWidths);

	    // Header cells
	    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
	    table.addCell(new PdfPCell(new Phrase("Emp Card No", headerFont)));
	    table.addCell(new PdfPCell(new Phrase("Emp Name", headerFont)));
	    table.addCell(new PdfPCell(new Phrase("Emp Role", headerFont)));
	    table.addCell(new PdfPCell(new Phrase("Task Title", headerFont)));
	    table.addCell(new PdfPCell(new Phrase("Status", headerFont)));
	    table.addCell(new PdfPCell(new Phrase("Report Details", headerFont)));
	    table.setHeaderRows(1); // ensures header is repeated on new pages

	    // Fill table rows
	    Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
	    for (Work w : works) {
	        String cardNo = w.getEmployee().getCardNumber();
	        String name = w.getEmployee().getFirstName() + " " + w.getEmployee().getLastName();
	       // String role = w.getEmployee().getJobRole();            // adjust if your entity uses different field
	        String taskTitle = w.getTitle();
	        String status = w.getStatus() != null ? w.getStatus().name() : "";
	        String reportDetails = w.getReportDetails() != null ? w.getReportDetails() : "";

	        table.addCell(new PdfPCell(new Phrase(cardNo, rowFont)));
	        table.addCell(new PdfPCell(new Phrase(name, rowFont)));
	      //  table.addCell(new PdfPCell(new Phrase(role, rowFont)));
	        table.addCell(new PdfPCell(new Phrase(taskTitle, rowFont)));
	        table.addCell(new PdfPCell(new Phrase(status, rowFont)));
	        table.addCell(new PdfPCell(new Phrase(reportDetails, rowFont)));
	    }
	    document.add(table);
	    document.close();

	    return baos.toByteArray();
	}
	
	public byte[] generateDailyReportForEmployees(List<DailyReport> reports, LocalDate date) throws Exception {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    Document document = new Document(PageSize.A4.rotate(), 20f, 20f, 20f, 20f);
	    PdfWriter.getInstance(document, baos);
	    document.open();

	    Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
	    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
	    Font rowFont    = FontFactory.getFont(FontFactory.HELVETICA, 11);

	    // Title
	    Paragraph title = new Paragraph("Daily Report Summary", titleFont);
	    title.setAlignment(Element.ALIGN_CENTER);
	    document.add(title);

	    Paragraph datePara = new Paragraph("Report Date: " + date.toString());
	    datePara.setAlignment(Element.ALIGN_RIGHT);
	    document.add(datePara);
	    document.add(new Paragraph("\n"));

	    // Table: 5 columns now (SrNo, Card No, Employee Name, Role, Summary)
	    PdfPTable table = new PdfPTable(5);
	    table.setWidthPercentage(100);
	    // column widths: Sr.No small, Card No small, Name medium, Role medium, Summary large
	    table.setWidths(new float[]{1f, 3f, 4f, 3f, 10f});
	    table.setSplitLate(false);   // try to avoid single-row split at bottom
	    table.setSplitRows(true);

	    // Header cells - style and mark them as header rows so they repeat on each page
	    PdfPCell h;

	    h = new PdfPCell(new Phrase("Sr. No.", headerFont));
	    h.setHorizontalAlignment(Element.ALIGN_CENTER);
	    h.setPadding(6f);
	    h.setBackgroundColor(BaseColor.YELLOW);
	    table.addCell(h);

	    h = new PdfPCell(new Phrase("Card No", headerFont));
	    h.setHorizontalAlignment(Element.ALIGN_CENTER);
	    h.setPadding(6f);
	    h.setBackgroundColor(BaseColor.YELLOW);
	    table.addCell(h);

	    h = new PdfPCell(new Phrase("Employee Name", headerFont));
	    h.setHorizontalAlignment(Element.ALIGN_CENTER);
	    h.setPadding(6f);
	    h.setBackgroundColor(BaseColor.YELLOW);
	    table.addCell(h);

	    h = new PdfPCell(new Phrase("Role", headerFont));
	    h.setHorizontalAlignment(Element.ALIGN_CENTER);
	    h.setPadding(6f);
	    h.setBackgroundColor(BaseColor.YELLOW);
	    table.addCell(h);

	    h = new PdfPCell(new Phrase("Summary", headerFont));
	    h.setHorizontalAlignment(Element.ALIGN_CENTER);
	    h.setBackgroundColor(BaseColor.YELLOW);
	    table.addCell(h);

	    // ensure header repeats on each page
	    table.setHeaderRows(1);

	    // Rows with serial number starting from 1 and incrementing across pages
	    int sr = 1;
	    for (DailyReport r : reports) {
	        PdfPCell c;

	        c = new PdfPCell(new Phrase(String.valueOf(sr++), rowFont));
	        c.setHorizontalAlignment(Element.ALIGN_CENTER);
	        c.setPadding(5f);
	        table.addCell(c);

	        c = new PdfPCell(new Phrase(
	                r.getEmployee() != null && r.getEmployee().getCardNumber() != null
	                        ? r.getEmployee().getCardNumber()
	                        : "", rowFont));
	        c.setPadding(5f);
	        table.addCell(c);

	        String fullName = "";
	        if (r.getEmployee() != null) {
	            fullName = (r.getEmployee().getFirstName() == null ? "" : r.getEmployee().getFirstName())
	                     + (r.getEmployee().getLastName() == null ? "" : " " + r.getEmployee().getLastName());
	            fullName = fullName.trim();
	        }
	        c = new PdfPCell(new Phrase(fullName, rowFont));
	        c.setPadding(5f);
	        table.addCell(c);

//	        c = new PdfPCell(new Phrase(r.getEmployee() != null && r.getEmployee().getJobRole() != null
//	                ? r.getEmployee().getJobRole()
//	                : "", rowFont));
//	        c.setPadding(5f);
//	        table.addCell(c);

	        // Summary cell: allow wrapping and cell splitting across pages
	        c = new PdfPCell(new Phrase(r.getSummary() != null ? r.getSummary() : "", rowFont));
	        c.setPadding(5f);
	        c.setNoWrap(false);          // allow wrapping
	        c.setUseAscender(true);
	        c.setUseDescender(true);
	        table.addCell(c);
	    }

	    document.add(table);

	    // Optionally show total count at the end (on same page if space; otherwise will be on next page)
	    Paragraph total = new Paragraph("Total reports: " + reports.size(), headerFont);
	    total.setAlignment(Element.ALIGN_RIGHT);
	    total.setSpacingBefore(10f);
	    document.add(total);

	    document.close();
	    return baos.toByteArray();
	}

}
