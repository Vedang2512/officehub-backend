package com.officehub.dto.dashboard;

public class EmployeePerformanceDTO {

    private Long employeeId;

    private String employeeName;

    private long totalAssignedTasks;

    private long completedTasks;

    private double completionPercentage;


    public EmployeePerformanceDTO() {
    }


    public EmployeePerformanceDTO(Long employeeId,
                                  String employeeName,
                                  long totalAssignedTasks,
                                  long completedTasks,
                                  double completionPercentage) {

        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.totalAssignedTasks = totalAssignedTasks;
        this.completedTasks = completedTasks;
        this.completionPercentage = completionPercentage;
    }


    public Long getEmployeeId() {
        return employeeId;
    }


    public String getEmployeeName() {
        return employeeName;
    }


    public long getTotalAssignedTasks() {
        return totalAssignedTasks;
    }


    public long getCompletedTasks() {
        return completedTasks;
    }


    public double getCompletionPercentage() {
        return completionPercentage;
    }
}