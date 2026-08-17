package com.officehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDashboardDTO {

    private Long managerId;

    private String managerName;

    // Teams
    private Long teamsManaged;

    private Long totalTeamMembers;

    // Tasks
    private Long totalTasksAssigned;

    private Long pendingTasks;

    private Long inProgressTasks;

    private Long completedTasks;

    private Long overdueTasks;

    // Performance
    private Double completionPercentage;

    // Workload
    private Double averageTasksPerMember;

}