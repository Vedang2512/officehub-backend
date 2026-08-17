package com.officehub.dto.dashboard;

import java.util.List;

public class OwnerDashboardDTO {

    private long totalEmployees;

    private long totalTasks;

    private long completedTasks;

    private long pendingTasks;

    private long inProgressTasks;
    
    private long totalManagers;

    private List<EmployeePerformanceDTO> employeePerformance;
    
    private List<RecentTaskDTO> recentTasks;
    
    


    public OwnerDashboardDTO() {
    }


    public OwnerDashboardDTO(long totalEmployees,
            long totalManagers,
            long totalTasks,
            long completedTasks,
            long pendingTasks,
            long inProgressTasks,
            List<EmployeePerformanceDTO> employeePerformance,
            List<RecentTaskDTO> recentTasks) {

		this.totalEmployees = totalEmployees;
		this.totalManagers = totalManagers;
		this.totalTasks = totalTasks;
		this.completedTasks = completedTasks;
		this.pendingTasks = pendingTasks;
		this.inProgressTasks = inProgressTasks;
		this.employeePerformance = employeePerformance;
		this.recentTasks = recentTasks;
	}


    public long getTotalEmployees() {
        return totalEmployees;
    }


    public long getTotalTasks() {
        return totalTasks;
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


    public List<EmployeePerformanceDTO> getEmployeePerformance() {
        return employeePerformance;
    }
    
    public List<RecentTaskDTO> getRecentTasks() {
        return recentTasks;
    }
    
    public long getTotalManagers() {
        return totalManagers;
    }
}