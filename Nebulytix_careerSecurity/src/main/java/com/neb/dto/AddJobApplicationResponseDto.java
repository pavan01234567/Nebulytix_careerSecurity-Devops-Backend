package com.neb.dto;


import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddJobApplicationResponseDto {
    private Long id;
    private LocalDate applicationDate;
    private String status;
}

