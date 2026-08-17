package com.officehub.service;

import java.util.List;


import com.officehub.dto.DepartmentAnalyticsDTO;
import com.officehub.dto.ManagerDashboardDTO;
import com.officehub.dto.TeamTaskSummaryDTO;


public interface AnalyticsService {

    TeamTaskSummaryDTO getTeamAnalytics(Long teamId, String email);

    ManagerDashboardDTO getManagerDashboard(String email);

    List<DepartmentAnalyticsDTO> getDepartmentAnalytics(String email);
    

}