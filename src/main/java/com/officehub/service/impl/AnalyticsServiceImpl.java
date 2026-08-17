package com.officehub.service.impl;

import java.util.List;


import org.springframework.stereotype.Service;

import com.officehub.dto.DepartmentAnalyticsDTO;
import com.officehub.dto.ManagerDashboardDTO;
import com.officehub.dto.TeamTaskSummaryDTO;
import com.officehub.repository.DepartmentRepository;
import com.officehub.repository.TaskRepository;
import com.officehub.repository.TeamMemberRepository;
import com.officehub.repository.TeamRepository;
import com.officehub.repository.UserRepository;
import com.officehub.service.AnalyticsService;
import com.officehub.entity.Team;
import com.officehub.entity.User;
import com.officehub.exception.ResourceNotFoundException;
import com.officehub.exception.UnauthorizedActionException;

import lombok.RequiredArgsConstructor;

import com.officehub.entity.Department;

import com.officehub.entity.Role;
import com.officehub.entity.TaskPriority;
import com.officehub.entity.TaskStatus;
import com.officehub.exception.UnauthorizedTeamAccessException;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;

    private final TaskRepository taskRepository;

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final DepartmentRepository departmentRepository;

    @Override
    public TeamTaskSummaryDTO getTeamAnalytics(Long teamId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Team not found"));

        if (user.getRole() == Role.OWNER) {

            if (!team.getOrganization().getId()
                    .equals(user.getOrganization().getId())) {
                throw new UnauthorizedTeamAccessException(
                        "You cannot access another organization's team.");
            }

        } else if (user.getRole() == Role.MANAGER) {

            if (!team.getManager().getId().equals(user.getId())) {
                throw new UnauthorizedTeamAccessException(
                        "You can only view analytics for your own teams.");
            }

        } else {

            throw new UnauthorizedTeamAccessException(
                    "You are not authorized to view team analytics.");
        }
        
        long totalTasks = taskRepository.countByTeam_Id(teamId);

        long pendingTasks = taskRepository.countByTeam_IdAndStatus(
                teamId,
                TaskStatus.TODO);

        long inProgressTasks = taskRepository.countByTeam_IdAndStatus(
                teamId,
                TaskStatus.IN_PROGRESS);

        long completedTasks = taskRepository.countByTeam_IdAndStatus(
                teamId,
                TaskStatus.COMPLETED);

        long highPriorityTasks = taskRepository.countByTeam_IdAndPriority(
                teamId,
                TaskPriority.HIGH);

        long mediumPriorityTasks = taskRepository.countByTeam_IdAndPriority(
                teamId,
                TaskPriority.MEDIUM);

        long lowPriorityTasks = taskRepository.countByTeam_IdAndPriority(
                teamId,
                TaskPriority.LOW);

        long totalMembers = teamMemberRepository.countByTeam_Id(teamId);

        double completionPercentage = 0.0;

        if (totalTasks > 0) {
            completionPercentage =
                    (completedTasks * 100.0) / totalTasks;
        }

        double averageTasksPerMember = 0.0;

        if (totalMembers > 0) {
            averageTasksPerMember =
                    (double) totalTasks / totalMembers;
        }

        return TeamTaskSummaryDTO.builder()
                .teamId(team.getId())
                .teamName(team.getName())

                .totalTasks(totalTasks)
                .pendingTasks(pendingTasks)
                .inProgressTasks(inProgressTasks)
                .completedTasks(completedTasks)

                .completionPercentage(completionPercentage)

                .highPriorityTasks(highPriorityTasks)
                .mediumPriorityTasks(mediumPriorityTasks)
                .lowPriorityTasks(lowPriorityTasks)

                .totalMembers(totalMembers)
                .averageTasksPerMember(averageTasksPerMember)

                .build();
    }

    @Override
    public ManagerDashboardDTO getManagerDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException(
                    "Only managers can access the manager dashboard.");
        }

        long teamsManaged = teamRepository.countByManager_Id(user.getId());

        long totalMembers = teamMemberRepository.countMembersByManager(user.getId());

        long totalTasks = taskRepository.countTasksByManager(user.getId());

        long todoTasks = taskRepository.countTasksByManagerAndStatus(
                user.getId(),
                TaskStatus.TODO);

        long inProgressTasks = taskRepository.countTasksByManagerAndStatus(
                user.getId(),
                TaskStatus.IN_PROGRESS);

        long completedTasks = taskRepository.countTasksByManagerAndStatus(
                user.getId(),
                TaskStatus.COMPLETED);

        long overdueTasks = taskRepository.countOverdueTasksByManager(
                user.getId(),
                TaskStatus.COMPLETED);
        
        double completionPercentage = 0.0;

        if (totalTasks > 0) {
            completionPercentage = (completedTasks * 100.0) / totalTasks;
        }

        double averageTasksPerMember = 0.0;

        if (totalMembers > 0) {
            averageTasksPerMember = (double) totalTasks / totalMembers;
        }

        return ManagerDashboardDTO.builder()
                .managerId(user.getId())
                .managerName(user.getFullName())

                .teamsManaged(teamsManaged)
                .totalTeamMembers(totalMembers)

                .totalTasksAssigned(totalTasks)
                .pendingTasks(todoTasks)
                .inProgressTasks(inProgressTasks)
                .completedTasks(completedTasks)
                .overdueTasks(overdueTasks)

                .completionPercentage(completionPercentage)
                .averageTasksPerMember(averageTasksPerMember)

                .build();
    }

    @Override
    public List<DepartmentAnalyticsDTO> getDepartmentAnalytics(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Only owners can access department analytics.");
        }

        List<Department> departments = departmentRepository
                .findByOrganizationId(user.getOrganization().getId());

        return departments.stream()
                .map(department -> {

                    long totalTeams =
                            teamRepository.countByDepartmentId(department.getId());

                    long totalEmployees =
                            teamMemberRepository.countEmployeesByDepartment(department.getId());

                    long totalTasks =
                            taskRepository.countByDepartmentId(department.getId());

                    long pendingTasks =
                            taskRepository.countByDepartmentIdAndStatus(
                                    department.getId(),
                                    TaskStatus.TODO);

                    long inProgressTasks =
                            taskRepository.countByDepartmentIdAndStatus(
                                    department.getId(),
                                    TaskStatus.IN_PROGRESS);

                    long completedTasks =
                            taskRepository.countByDepartmentIdAndStatus(
                                    department.getId(),
                                    TaskStatus.COMPLETED);

                    double completionPercentage =
                            totalTasks == 0
                                    ? 0.0
                                    : (completedTasks * 100.0) / totalTasks;

                    double averageTasksPerTeam =
                            totalTeams == 0
                                    ? 0.0
                                    : (double) totalTasks / totalTeams;

                    return DepartmentAnalyticsDTO.builder()
                            .departmentId(department.getId())
                            .departmentName(department.getName())

                            .totalTeams(totalTeams)
                            .totalEmployees(totalEmployees)

                            .totalTasks(totalTasks)
                            .pendingTasks(pendingTasks)
                            .inProgressTasks(inProgressTasks)
                            .completedTasks(completedTasks)

                            .completionPercentage(completionPercentage)
                            .averageTasksPerTeam(averageTasksPerTeam)

                            .build();

                })
                .toList();
    }
    

}