package com.neb.dto;

import java.time.LocalDate;


import lombok.Data;

@Data
public class AddWorkRequestDto {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Long employeeId;
}
