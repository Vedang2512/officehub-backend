package com.officehub.controller;

import java.util.List;
import com.officehub.dto.UpdateTaskRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.officehub.dto.CreateTaskRequestDTO;
import com.officehub.dto.TaskResponseDTO;
import com.officehub.dto.UpdateTaskStatusRequestDTO;
import com.officehub.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {


    private final TaskService taskService;


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody CreateTaskRequestDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                taskService.createTask(request, email)
        );
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<TaskResponseDTO>> getMyTasks(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                taskService.getMyTasks(email)
        );
    }


    @GetMapping("/organization")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<TaskResponseDTO>> getOrganizationTasks(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                taskService.getOrganizationTasks(email)
        );
    }
    
    


    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequestDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                taskService.updateTaskStatus(
                        taskId,
                        request,
                        email
                )
        );
    }
    
    @GetMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long taskId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                taskService.getTaskById(
                        taskId,
                        authentication.getName()
                )
        );
    }
    
    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequestDTO request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                taskService.updateTask(
                        taskId,
                        request,
                        authentication.getName()
                )
        );
    }
    
    
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            Authentication authentication
    ) {

        taskService.deleteTask(
                taskId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}