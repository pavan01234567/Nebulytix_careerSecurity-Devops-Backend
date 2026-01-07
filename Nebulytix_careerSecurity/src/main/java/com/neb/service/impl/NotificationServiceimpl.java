package com.neb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neb.entity.EmployeeLeaves;
import com.neb.entity.Notification;
import com.neb.repo.NotificationRepository;
import com.neb.service.NotificationService;
import com.neb.util.ApprovalStatus;
import com.neb.util.NotificationType;

@Service
public class NotificationServiceimpl implements NotificationService 
{


    @Autowired
    private NotificationRepository notificationRepo;
    @Override
    public void notifyHrLeaveApplied(EmployeeLeaves leave) {

        Notification n = new Notification();

        // ✅ MOST IMPORTANT LINE
        n.setEmployee(leave.getEmployee());
        System.out.println("Employee ID = " + leave.getEmployee().getId());

        n.setRole("HR");
        n.setStatus("UNREAD");
        n.setType(NotificationType.LEAVE_APPLIED);

        n.setMessage(
            leave.getEmployee().getFirstName()
            + " applied for "
            + leave.getLeaveType()
            + " from "
            + leave.getStartDate()
            + " to "
            + leave.getEndDate()
        );

        notificationRepo.save(n);
    }

    @Override
    public void notifyEmployeeLeaveDecision(EmployeeLeaves leave) {

        Notification n = new Notification();

        // ✅ SET ENTITY, NOT ID
        n.setEmployee(leave.getEmployee());

        n.setRole("EMPLOYEE");
        n.setStatus("UNREAD");

        if (leave.getLeaveStatus() == ApprovalStatus.APPROVED) {
            n.setType(NotificationType.LEAVE_APPROVED);
            n.setMessage("Your leave request has been APPROVED");
        } else {
            n.setType(NotificationType.LEAVE_REJECTED);
            n.setMessage("Your leave request has been REJECTED");
        }

        notificationRepo.save(n);
    }


	}

