package com.officehub.service;

import com.officehub.dto.dashboard.EmployeeDashboardDTO;
import java.util.List;
import com.officehub.dto.dashboard.TaskStatusCountDTO;
import com.officehub.dto.dashboard.ManagerDashboardDTO;
import com.officehub.dto.dashboard.OwnerDashboardDTO;

public interface DashboardService {


    OwnerDashboardDTO getOwnerDashboard(Long userId);


    ManagerDashboardDTO getManagerDashboard(Long userId);


    EmployeeDashboardDTO getEmployeeDashboard(Long userId);
    
    List<TaskStatusCountDTO> getTaskStatusCounts(Long userId);

}