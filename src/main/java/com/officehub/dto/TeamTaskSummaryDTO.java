package com.officehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamTaskSummaryDTO {

    private Long teamId;

    private String teamName;

    private Long totalTasks;

    private Long pendingTasks;

    private Long inProgressTasks;

    private Long completedTasks;

    private Double completionPercentage;

    private Long highPriorityTasks;

    private Long mediumPriorityTasks;

    private Long lowPriorityTasks;

    private Long totalMembers;

    private Double averageTasksPerMember;

}