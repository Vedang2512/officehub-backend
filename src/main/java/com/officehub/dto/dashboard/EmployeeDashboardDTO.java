package com.officehub.dto.dashboard;

public class EmployeeDashboardDTO {

    private long assignedTasks;

    private long completedTasks;

    private long pendingTasks;

    private double progressPercentage;


    public EmployeeDashboardDTO() {
    }


    public EmployeeDashboardDTO(long assignedTasks,
                                long completedTasks,
                                long pendingTasks,
                                double progressPercentage) {

        this.assignedTasks = assignedTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.progressPercentage = progressPercentage;
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


    public double getProgressPercentage() {
        return progressPercentage;
    }
}