package com.officehub.dto;

import com.officehub.entity.TaskStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateTaskStatusRequestDTO {

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    public UpdateTaskStatusRequestDTO() {
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}