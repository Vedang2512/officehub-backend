package com.officehub.controller;

import org.springframework.http.ResponseEntity;
import java.util.List;
import com.officehub.dto.dashboard.TaskStatusCountDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.officehub.dto.dashboard.EmployeeDashboardDTO;
import com.officehub.dto.dashboard.ManagerDashboardDTO;
import com.officehub.dto.dashboard.OwnerDashboardDTO;
import com.officehub.service.DashboardService;
import com.officehub.repository.UserRepository;


@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {


    private final DashboardService dashboardService;
    private final UserRepository userRepository;


    public DashboardController(DashboardService dashboardService,
            UserRepository userRepository) {

		this.dashboardService = dashboardService;
		this.userRepository = userRepository;
	}


    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OwnerDashboardDTO> getOwnerDashboard(
            Authentication authentication) {


    	Long userId = getUserId(authentication);


        return ResponseEntity.ok(
                dashboardService.getOwnerDashboard(userId)
        );
    }
    
    @GetMapping("/analytics/task-status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<TaskStatusCountDTO>> getTaskStatusAnalytics(
            Authentication authentication) {

        Long userId = getUserId(authentication);

        System.out.println(">>> Analytics endpoint called");

        return ResponseEntity.ok(
                dashboardService.getTaskStatusCounts(userId)
        );
    }


    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerDashboardDTO> getManagerDashboard(
            Authentication authentication) {


    	Long userId = getUserId(authentication);


        return ResponseEntity.ok(
                dashboardService.getManagerDashboard(userId)
        );
    }


    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<EmployeeDashboardDTO> getEmployeeDashboard(
            Authentication authentication) {


    	Long userId = getUserId(authentication);


        return ResponseEntity.ok(
                dashboardService.getEmployeeDashboard(userId)
        );
    }
    
    private Long getUserId(Authentication authentication) {

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

}