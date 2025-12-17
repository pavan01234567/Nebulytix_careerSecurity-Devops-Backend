package com.neb.dto;

import java.time.LocalDate;
import com.neb.constants.WorkStatus;
import com.neb.entity.Work;

import lombok.Data;

@Data
public class WorkResponseDto {
	    private Long id;
	    private String title;
	    private String description;
	    private LocalDate assignedDate;
	    private LocalDate dueDate;
	    private WorkStatus status;
	    private String reportDetails;
	    private LocalDate submittedDate;
	    private String reportAttachmentUrl;
	    private String attachmentUrl;
	    private Long employeeId;
	    private String employeeName;
	    private String employeeEmail;
		public static WorkResponseDto fromEntity(Work work) {
			// TODO Auto-generated method stub
			return null;
		}
}
