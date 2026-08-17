package com.officehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentAnalyticsDTO {

    private Long departmentId;

    private String departmentName;

    // Team Statistics
    private Long totalTeams;

    private Long totalEmployees;

    // Task Statistics
    private Long totalTasks;

    private Long pendingTasks;

    private Long inProgressTasks;

    private Long completedTasks;

    // Productivity
    private Double completionPercentage;

    // Workload
    private Double averageTasksPerTeam;

}