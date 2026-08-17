package com.officehub.dto.dashboard;
import com.officehub.entity.TaskStatus;

public class TaskStatusCountDTO {

	private TaskStatus status;
    private Long count;

    public TaskStatusCountDTO() {
    }

    public TaskStatusCountDTO(TaskStatus status, Long count) {
        this.status = status;
        this.count = count;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}