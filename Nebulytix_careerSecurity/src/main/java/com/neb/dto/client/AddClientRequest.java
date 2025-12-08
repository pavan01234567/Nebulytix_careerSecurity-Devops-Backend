package com.neb.dto.client;


import lombok.Data;

@Data
public class AddClientRequest {

    private String companyName;
    private String contactPerson;
    private String contactEmail;
    private String phone;
    private String alternatePhone;

    private String address;
    private String website;
    private String industryType; // IT, Finance, Construction, etc.
    private String gstNumber;
}
