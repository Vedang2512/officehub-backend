package com.officehub.service;

import java.util.List;


import com.officehub.dto.CreateTaskRequestDTO;
import com.officehub.dto.TaskResponseDTO;
import com.officehub.dto.UpdateTaskRequestDTO;
import com.officehub.dto.UpdateTaskStatusRequestDTO;


public interface TaskService {
	

    TaskResponseDTO createTask(CreateTaskRequestDTO request, String creatorEmail);

    List<TaskResponseDTO> getMyTasks(String employeeEmail);

    List<TaskResponseDTO> getOrganizationTasks(String ownerEmail);

    TaskResponseDTO updateTaskStatus(
            Long taskId,
            UpdateTaskStatusRequestDTO request,
            String employeeEmail
    );
    
    TaskResponseDTO getTaskById(Long taskId, String userEmail);
    
    TaskResponseDTO updateTask(
            Long taskId,
            UpdateTaskRequestDTO request,
            String userEmail
    );
    
    void deleteTask(Long taskId, String userEmail);
}