package com.officehub.dto.dashboard;

public class RecentTaskDTO {

    private Long id;

    private String title;

    private String assignedTo;

    private String status;

    private String priority;

    public RecentTaskDTO() {
    }

    public RecentTaskDTO(Long id,
                         String title,
                         String assignedTo,
                         String status,
                         String priority) {

        this.id = id;
        this.title = title;
        this.assignedTo = assignedTo;
        this.status = status;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }
}