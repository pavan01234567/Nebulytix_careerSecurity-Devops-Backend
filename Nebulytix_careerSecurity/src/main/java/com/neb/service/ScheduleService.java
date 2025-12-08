/**
 * --------------------------------------------------------------
 * Purpose :
 *   Defines service-level operations for managing schedules.
 *
 * Description :
 *   - Declares methods to create, retrieve, and delete schedules.
 *   - Implemented by ScheduleServiceImpl.
 * --------------------------------------------------------------
 */

package com.neb.service;

import java.util.List;
import com.neb.entity.Schedule;

public interface ScheduleService {

    // ✅ Create or update a schedule
    Schedule createSchedule(Schedule schedule);

    // ✅ Get all schedules
    List<Schedule> getAllSchedules();

    // ✅ Get schedules by employee ID
    List<Schedule> getSchedulesByEmployee(Long employeeId);

    // ✅ Delete a schedule by ID
    void deleteSchedule(Long id);
}
