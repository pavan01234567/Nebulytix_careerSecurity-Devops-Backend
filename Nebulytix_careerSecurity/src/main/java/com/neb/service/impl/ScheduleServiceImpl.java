package com.neb.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neb.entity.Schedule;
import com.neb.exception.CustomeException; 
import com.neb.repo.ScheduleRepository;
import com.neb.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    public List<Schedule> getSchedulesByEmployee(Long employeeId) {
        List<Schedule> schedules = scheduleRepository.findByEmployeeId(employeeId);
        if (schedules.isEmpty()) {
            
            throw new CustomeException("No schedules found for employee ID: " + employeeId);
        }
        return schedules;
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
