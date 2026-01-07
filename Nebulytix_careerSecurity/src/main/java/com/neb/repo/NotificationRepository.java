package com.neb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.Notification;
import com.neb.util.NotificationStatus;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRoleAndStatus(String role, NotificationStatus status);

    List<Notification> findByEmployee_Id(Long employeeId);

    
    
}
