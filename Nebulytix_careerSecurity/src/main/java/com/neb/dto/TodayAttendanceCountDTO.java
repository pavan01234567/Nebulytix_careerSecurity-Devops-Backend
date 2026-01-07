package com.neb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodayAttendanceCountDTO {

    private long presentToday;
    private long wfhToday;
}
