package com.neb.dto.client;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientDto {
    private Long id;
    private String companyName;
    private String contactPerson;
    private String contactEmail;
    private String phone;
    private String alternatePhone;
    private String address;
    private String website;
    private String industryType;
    private String gstNumber;
    private String status;
    private LocalDate createdDate;
    private LocalDate updatedDate;
}
