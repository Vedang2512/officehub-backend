package com.officehub.dto.dashboard;

public class ManagerDashboardDTO {

    private long teamMembers;

    private long assignedTasks;

    private long completedTasks;

    private long pendingTasks;

    private long inProgressTasks;


    public ManagerDashboardDTO() {
    }


    public ManagerDashboardDTO(long teamMembers,
                               long assignedTasks,
                               long completedTasks,
                               long pendingTasks,
                               long inProgressTasks) {

        this.teamMembers = teamMembers;
        this.assignedTasks = assignedTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.inProgressTasks = inProgressTasks;
    }


    public long getTeamMembers() {
        return teamMembers;
    }


    public long getAssignedTasks() {
        return assignedTasks;
    }


    public long getCompletedTasks() {
        return completedTasks;
    }


    public long getPendingTasks() {
        return pendingTasks;
    }


    public long getInProgressTasks() {
        return inProgressTasks;
    }
}