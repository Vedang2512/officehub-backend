package com.officehub.service.impl;

import org.springframework.stereotype.Service;
import com.officehub.dto.dashboard.RecentTaskDTO;


import com.officehub.dto.dashboard.EmployeeDashboardDTO;
import com.officehub.dto.dashboard.ManagerDashboardDTO;
import com.officehub.dto.dashboard.OwnerDashboardDTO;
import com.officehub.service.DashboardService;
import com.officehub.repository.TaskRepository;
import com.officehub.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import com.officehub.dto.dashboard.TaskStatusCountDTO;

import java.util.stream.Collectors;

import com.officehub.entity.Role;
import com.officehub.entity.TaskStatus;
import com.officehub.entity.User;

import com.officehub.dto.dashboard.EmployeePerformanceDTO;

@Service
public class DashboardServiceImpl implements DashboardService {


    private final UserRepository userRepository;

    private final TaskRepository taskRepository;


    public DashboardServiceImpl(UserRepository userRepository,
                                TaskRepository taskRepository) {

        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }


    @Override
    public OwnerDashboardDTO getOwnerDashboard(Long userId) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (owner.getOrganization() == null) {
            return new OwnerDashboardDTO(
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }

        Long organizationId = owner.getOrganization().getId();

        long totalEmployees =
                userRepository.countByOrganizationIdAndRole(
                        organizationId,
                        Role.EMPLOYEE
                );

        long totalManagers =
                userRepository.countByOrganizationIdAndRole(
                        organizationId,
                        Role.MANAGER
                );

        long totalTasks =
                taskRepository.countByOrganizationId(organizationId);

        long completedTasks =
                taskRepository.countByOrganizationIdAndStatus(
                        organizationId,
                        TaskStatus.COMPLETED
                );

        long pendingTasks =
                taskRepository.countByOrganizationIdAndStatus(
                        organizationId,
                        TaskStatus.TODO
                );

        long inProgressTasks =
                taskRepository.countByOrganizationIdAndStatus(
                        organizationId,
                        TaskStatus.IN_PROGRESS
                );

        List<User> employees =
                userRepository.findByOrganizationId(organizationId);

        List<EmployeePerformanceDTO> performance =
                employees.stream()
                        .filter(user ->
                                user.getRole() == Role.EMPLOYEE ||
                                user.getRole() == Role.MANAGER
                        )
                        .map(employee -> {

                            long assigned =
                                    taskRepository.countByAssignedToId(
                                            employee.getId()
                                    );

                            long completed =
                                    taskRepository.countByAssignedToIdAndStatus(
                                            employee.getId(),
                                            TaskStatus.COMPLETED
                                    );

                            double percentage = assigned == 0
                                    ? 0
                                    : ((double) completed / assigned) * 100;

                            return new EmployeePerformanceDTO(
                                    employee.getId(),
                                    employee.getFullName(),
                                    assigned,
                                    completed,
                                    percentage
                            );

                        })
                        .collect(Collectors.toList());

        List<RecentTaskDTO> recentTasks =
                taskRepository
                        .findTop5ByOrganizationIdOrderByCreatedAtDesc(organizationId)
                        .stream()
                        .map(task -> new RecentTaskDTO(
                                task.getId(),
                                task.getTitle(),
                                task.getAssignedTo().getFullName(),
                                task.getStatus().name(),
                                task.getPriority().name()
                        ))
                        .collect(Collectors.toList());

        return new OwnerDashboardDTO(
                totalEmployees,
                totalManagers,
                totalTasks,
                completedTasks,
                pendingTasks,
                inProgressTasks,
                performance,
                recentTasks
        );
    }


    
    @Override
    public ManagerDashboardDTO getManagerDashboard(Long userId) {

        User manager = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (manager.getOrganization() == null) {
            return new ManagerDashboardDTO(
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }

        Long organizationId = manager.getOrganization().getId();


        long teamMembers =
                userRepository.countByOrganizationIdAndRole(
                        organizationId,
                        Role.EMPLOYEE
                );


        // Only tasks assigned to this manager
        long assignedTasks =
                taskRepository.countByAssignedToId(userId);


        long completedTasks =
                taskRepository.countByAssignedToIdAndStatus(
                        userId,
                        TaskStatus.COMPLETED
                );


        long pendingTasks =
                taskRepository.countByAssignedToIdAndStatus(
                        userId,
                        TaskStatus.TODO
                );


        long inProgressTasks =
                taskRepository.countByAssignedToIdAndStatus(
                        userId,
                        TaskStatus.IN_PROGRESS
                );


        return new ManagerDashboardDTO(
                teamMembers,
                assignedTasks,
                completedTasks,
                pendingTasks,
                inProgressTasks
        );
    }
    



    @Override
    public EmployeeDashboardDTO getEmployeeDashboard(Long userId) {

        long assignedTasks =
                taskRepository.countByAssignedToId(userId);


        long completedTasks =
                taskRepository.countByAssignedToIdAndStatus(
                        userId,
                        TaskStatus.COMPLETED
                );


        long pendingTasks =
                taskRepository.countByAssignedToIdAndStatus(
                        userId,
                        TaskStatus.TODO
                );


        double progressPercentage =
                assignedTasks == 0
                        ? 0
                        : ((double) completedTasks / assignedTasks) * 100;


        return new EmployeeDashboardDTO(
                assignedTasks,
                completedTasks,
                pendingTasks,
                progressPercentage
        );
    }
    
    @Override
    public List<TaskStatusCountDTO> getTaskStatusCounts(Long userId) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (owner.getOrganization() == null) {
            return new ArrayList<>();
        }

        Long organizationId = owner.getOrganization().getId();

        return taskRepository.getTaskStatusCountsByOrganization(organizationId);
    }
    
    

}