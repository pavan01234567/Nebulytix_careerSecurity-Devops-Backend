package com.neb.service;

import com.neb.entity.EmployeeLeaves;

public interface NotificationService {

	public void notifyHrLeaveApplied(EmployeeLeaves leave);
	 public void notifyEmployeeLeaveDecision(EmployeeLeaves leave);
}
