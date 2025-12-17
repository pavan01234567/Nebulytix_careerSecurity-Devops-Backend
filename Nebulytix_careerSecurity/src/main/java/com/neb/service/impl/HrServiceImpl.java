package com.neb.service.impl;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.neb.dto.AddEmployeeRequestDto;
import com.neb.dto.AddEmployeeResponseDto;
import com.neb.dto.AddJobRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.JobDetailsDto;
import com.neb.dto.PayslipDto;
import com.neb.dto.UpdateEmployeeRequestDto;
import com.neb.dto.UpdatePasswordRequestDto;
import com.neb.entity.DailyReport;
import com.neb.entity.Employee;
import com.neb.entity.Job;
import com.neb.entity.JobApplication;
import com.neb.entity.Payslip;
import com.neb.exception.CustomeException;
import com.neb.repo.DailyReportRepository;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.JobApplicationRepository;
import com.neb.repo.JobRepository;
import com.neb.repo.PayslipRepository;
import com.neb.service.EmailService;
import com.neb.service.HrService;
import com.neb.util.ReportGeneratorPdf;

@Service
public class HrServiceImpl implements HrService {

    @Autowired
    private EmployeeRepository empRepo;

    @Autowired
    private PayslipRepository payslipRepo;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DailyReportRepository dailyReportRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper mapper;

    @Value("${daily-report.folder-path}")
    private String dailyReportFolderPath;

    // ======================= EMPLOYEE METHODS =========================

    
//    @Override
//    public List<EmployeeDetailsResponseDto> getEmployeeList() {
//        List<Employee> employees = empRepo.findByLoginRoleNotIn(List.of("admin", "hr"));
//        if (employees.isEmpty()) throw new CustomeException("No employees found");
//        return employees.stream().map(emp -> mapper.map(emp, EmployeeDetailsResponseDto.class)).collect(Collectors.toList());
//    }

    @Override
    public EmployeeDetailsResponseDto getEmployee(Long id) {
        Employee emp = empRepo.findById(id).orElseThrow(() -> new CustomeException("Employee not found with id: " + id));
        return mapper.map(emp, EmployeeDetailsResponseDto.class);
    }

    @Override
    public String deleteById(Long id) {
        empRepo.deleteById(id);
        return "Employee with ID " + id + " deleted successfully";
    }

    @Override
    public EmployeeDetailsResponseDto addAttendence(Long id, int days) {
        Employee emp = empRepo.findById(id).orElseThrow(() -> new CustomeException("Employee not found"));
        emp.setDaysPresent(days);
        return mapper.map(empRepo.save(emp), EmployeeDetailsResponseDto.class);
    }

    @Override
    public EmployeeDetailsResponseDto updateEmployee(Long id, UpdateEmployeeRequestDto updateReq) {
        Employee emp = empRepo.findById(id).orElseThrow(() -> new CustomeException("Employee not found"));

        if (updateReq.getFirstName() != null) emp.setFirstName(updateReq.getFirstName());
        if (updateReq.getLastName() != null) emp.setLastName(updateReq.getLastName());
 //       if (updateReq.getEmail() != null) emp.setEmail(updateReq.getEmail());
        if (updateReq.getMobile() != null) emp.setMobile(updateReq.getMobile());
        if (updateReq.getSalary() != null) emp.setSalary(updateReq.getSalary());
        if (updateReq.getPaidLeaves() != 0) emp.setPaidLeaves(updateReq.getPaidLeaves());

//        if (updateReq.getBankAccountNumber() != null) emp.setBankAccountNumber(updateReq.getBankAccountNumber());
//        if (updateReq.getIfscCode() != null) emp.setIfscCode(updateReq.getIfscCode());
//        if (updateReq.getBankName() != null) emp.setBankName(updateReq.getBankName());
//        if (updateReq.getPfNumber() != null) emp.setPfNumber(updateReq.getPfNumber());
//        if (updateReq.getPanNumber() != null) emp.setPanNumber(updateReq.getPanNumber());
//        if (updateReq.getUanNumber() != null) emp.setUanNumber(updateReq.getUanNumber());
//        if (updateReq.getEpsNumber() != null) emp.setEpsNumber(updateReq.getEpsNumber());
//        if (updateReq.getEsiNumber() != null) emp.setEsiNumber(updateReq.getEsiNumber());

        return mapper.map(empRepo.save(emp), EmployeeDetailsResponseDto.class);
    }


    // ======================= PAYSLIP METHODS =========================
    @Override
    public byte[] downloadPayslip(Long payslipId) throws Exception {
        Payslip p = payslipRepo.findById(payslipId).orElseThrow(() -> new CustomeException("Payslip not found"));
        return Files.readAllBytes(Paths.get(p.getPdfPath()));
    }

    @Override
    public List<PayslipDto> listPayslipsForEmployee(Long employeeId) {
        List<Payslip> payslips = payslipRepo.findByEmployeeId(employeeId);
        if (payslips.isEmpty()) throw new CustomeException("No payslips found");
        return payslips.stream().map(PayslipDto::fromEntity).collect(Collectors.toList());
    }

    // ======================= JOB METHODS =========================
    @Override
    public JobDetailsDto addJob(AddJobRequestDto jobRequestDto) {
        Job job = mapper.map(jobRequestDto, Job.class);
        job.setIsActive(true);
        job.setPostedDate(jobRequestDto.getPostedDate() != null ? jobRequestDto.getPostedDate() : LocalDate.now());
        return mapper.map(jobRepository.save(job), JobDetailsDto.class);
    }

    @Override
    public List<JobDetailsDto> getAllJobs() {
        LocalDate today = LocalDate.now();
        return jobRepository.findAll().stream().map(job -> {
            job.setIsActive(job.getClosingDate() == null || !job.getClosingDate().isBefore(today));
            return mapper.map(job, JobDetailsDto.class);
        }).collect(Collectors.toList());
    }

    @Override
    public String deleteJob(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        jobRepository.delete(job);
        return "Job deleted successfully";
    }

    // ======================= DAILY REPORT =========================
    @Override
    public String generateDailyReport(LocalDate reportDate) {
        List<DailyReport> reports = dailyReportRepository.findByReportDate(reportDate);
        if (reports.isEmpty()) return "No daily reports found for date: " + reportDate;

        try {
            byte[] pdfBytes = new ReportGeneratorPdf().generateDailyReportForEmployees(reports, reportDate);

            Path folder = Paths.get(dailyReportFolderPath);
            if (!Files.exists(folder)) Files.createDirectories(folder);

            String fileName = "daily-report-" + reportDate + ".pdf";
            Path filePath = folder.resolve(fileName);

            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(pdfBytes);
            }

            String fileUrl = "/reports/daily/" + fileName;
            reports.forEach(r -> r.setDailyReportUrl(fileUrl));
            dailyReportRepository.saveAll(reports);

            return fileUrl;

        } catch (Exception e) {
            throw new CustomeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    @Override
    public String getDailyReportUrl(LocalDate reportDate) {
        List<DailyReport> reports = dailyReportRepository.findByReportDate(reportDate);
        return reports.isEmpty() ? null : reports.get(0).getDailyReportUrl();
    }

    // ======================= JOB APPLICATION =========================
    @Override
    public void updateJobApplicationStatus(Long applicationId, Boolean status) {
        JobApplication app = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status ? "SHORTLISTED" : "REJECTED");
        jobApplicationRepository.save(app);
    }

    @Override
    public List<JobApplication> sendEmailsToShortlisted(String subject, String message) {
        List<JobApplication> shortlisted = jobApplicationRepository.findByStatus("SHORTLISTED");

        if (shortlisted.isEmpty()) {
            throw new RuntimeException("No shortlisted applicants found");
        }

        for (JobApplication app : shortlisted) {
            emailService.sendApplicationMail(app.getEmail(), subject, message);
            app.setStatus("INVITED"); // update status after sending email
            jobApplicationRepository.save(app);
        }

        return shortlisted; // return updated applicants
    }

    @Override
    public List<JobApplication> sendEmailsToRejected(String subject, String message) {
        List<JobApplication> rejected = jobApplicationRepository.findByStatus("REJECTED");

        if (rejected.isEmpty()) {
            throw new RuntimeException("No rejected applicants found");
        }

        for (JobApplication app : rejected) {
            emailService.sendApplicationMail(app.getEmail(), subject, message);
            app.setStatus("TERMINATED"); // update status after sending email
            jobApplicationRepository.save(app);
        }

        return rejected; // return updated applicants
    }


    @Override
    public void sendEmailToSingleApplicant(Long applicantId, String subject, String message) {
        String email = jobApplicationRepository.findEmailByApplicationId(applicantId);
        if (email == null) throw new RuntimeException("Applicant not found for ID: " + applicantId);
        emailService.sendEmail(email, subject, message);
    }

    @Override
    public void sendInvitedEmailAndUpdateStatus(Long applicantId, String subject, String message) {
        JobApplication app = jobApplicationRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        emailService.sendApplicationMail(app.getEmail(), subject, message);
        app.setStatus("INVITED");
        jobApplicationRepository.save(app);
    }

    @Override
    public void sendRejectedEmailAndUpdateStatus(Long applicantId, String subject, String message) {
        JobApplication app = jobApplicationRepository.findById(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        emailService.sendApplicationMail(app.getEmail(), subject, message);
        app.setStatus("TERMINATED");
        jobApplicationRepository.save(app);
    }

	@Override
	public void deletePayslip(Long id) {
		
		// 1. Find payslip
        Payslip payslip = payslipRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payslip not found with ID: " + id));

        // 2. Delete file from filesystem
        if (payslip.getPdfPath() != null) {

            File file = new File(payslip.getPdfPath());

            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new RuntimeException("Failed to delete file from disk: " + payslip.getPdfPath());
                }
            }
        }

        // 3. Delete DB record
        payslipRepo.delete(payslip);

        
    
	}
}
