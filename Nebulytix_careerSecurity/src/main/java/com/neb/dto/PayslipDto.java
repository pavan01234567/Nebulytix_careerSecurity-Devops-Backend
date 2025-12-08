package com.neb.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.neb.entity.Employee;
import com.neb.entity.Payslip;

import lombok.Data;

@Data
public class PayslipDto {

    private Long id;
    private String payslipMonth;
    private LocalDateTime generatedDate;

    // salary breakdown
    private Double basic;
    private Double hra;
    private Double flexi;
    private Double grossSalary;

    // deductions breakdown
    private Double pfDeduction;
    private Double profTaxDeduction;
    private Double totalDeductions;

    private Double netSalary;

    // file info
    private String fileName;
    private String pdfPath;

    // employee details
    private Long employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeEmail;
    private String employeeBankAccountNumber;
    private String employeeBankName;
    private String employeePfNumber;
    private String employeePanNumber;
    private String employeeUanNumber;
    
    //newly added
    private String location;
    private Double balance;
    private Double aggrgDeduction;
    private Double incHdSalary;      // note: camelCase, maybe rename from “IncHdSalary”
    private Double taxCredit;

    // mapping from entity
    public static PayslipDto fromEntity(Payslip p) {
        PayslipDto dto = new PayslipDto();
        dto.setId(p.getId());
        dto.setPayslipMonth(p.getPayslipMonth());
        dto.setGeneratedDate(p.getGeneratedDate());
        dto.setBasic(p.getBasic());
        dto.setHra(p.getHra());
        dto.setFlexi(p.getFlexi());
        dto.setGrossSalary(p.getGrossSalary());
        dto.setPfDeduction(p.getPfDeduction());
        dto.setProfTaxDeduction(p.getProfTaxDeduction());
        dto.setTotalDeductions(p.getTotalDeductions());
        dto.setNetSalary(p.getNetSalary());
        dto.setFileName(p.getFileName());
        dto.setPdfPath(p.getPdfPath());
        
        dto.setLocation(p.getLocation());
        dto.setBalance(p.getBalance());
        dto.setAggrgDeduction(p.getAggrgDeduction());
        dto.setIncHdSalary(p.getIncHdSalary());
        dto.setTaxCredit(p.getTaxCredit());

        if (p.getEmployee() != null) {
            Employee emp = p.getEmployee();
            dto.setEmployeeId(emp.getId());
            dto.setEmployeeFirstName(emp.getFirstName());
            dto.setEmployeeLastName(emp.getLastName());
            dto.setEmployeeEmail(emp.getEmail());
//            dto.setEmployeeBankAccountNumber(emp.getBankAccountNumber());
//            dto.setEmployeeBankName(emp.getBankName());
//            dto.setEmployeePfNumber(emp.getPfNumber());
//            dto.setEmployeePanNumber(emp.getPanNumber());
//            dto.setEmployeeUanNumber(emp.getUanNumber());
        }

        return dto;
    }
}
