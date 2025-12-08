/**
 * --------------------------------------------------------------
 * Purpose :
 *   Provides database access for Schedule entity.
 *
 * Description :
 *   - Extends JpaRepository to handle CRUD operations.
 *   - Custom finder method to fetch schedules by employee ID.
 * --------------------------------------------------------------
 */

package com.neb.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.neb.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByEmployeeId(Long employeeId);
}
