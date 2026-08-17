package com.officehub.service.impl;

import java.util.List;

import com.officehub.dto.TeamEventDTO;
import com.officehub.websocket.TeamWebSocketService;

import com.officehub.entity.Role;
import com.officehub.exception.UnauthorizedTeamAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officehub.dto.TeamMemberResponseDTO;
import com.officehub.dto.TeamRequestDTO;
import com.officehub.dto.TeamResponseDTO;
import com.officehub.repository.DepartmentRepository;
import com.officehub.repository.OrganizationRepository;
import com.officehub.repository.TeamMemberRepository;
import com.officehub.repository.TeamRepository;
import com.officehub.repository.UserRepository;
import com.officehub.service.TeamService;
import com.officehub.entity.Team;
import com.officehub.entity.TeamMember;
import com.officehub.entity.User;
import com.officehub.exception.EmployeeAlreadyAssignedException;
import com.officehub.exception.OrganizationMismatchException;
import com.officehub.exception.ResourceNotFoundException;
import com.officehub.exception.TeamAlreadyExistsException;

import com.officehub.service.NotificationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;

    private final OrganizationRepository organizationRepository;
    
    private final NotificationService notificationService;
    
    
    private final TeamWebSocketService teamWebSocketService;

    @Override
    public TeamResponseDTO createTeam(
            TeamRequestDTO request,
            String userEmail) {

    	User user = getUser(userEmail);

    	if (user.getRole() != Role.OWNER) {
    	    throw new UnauthorizedTeamAccessException(
    	            "Only the organization owner can create teams."
    	    );
    	}

        if (teamRepository.existsByNameAndOrganizationId(
                request.getName(),
                user.getOrganization().getId())) {

            throw new TeamAlreadyExistsException("Team already exists.");
        }

        Team team = new Team();

        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setOrganization(user.getOrganization());

        // Optional department
        if (request.getDepartmentId() != null) {

            team.setDepartment(
                    departmentRepository.findById(request.getDepartmentId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Department not found")));
        }

        // Optional manager
        if (request.getManagerId() != null) {

            User manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Manager not found"));

            if (!manager.getOrganization().getId()
                    .equals(user.getOrganization().getId())) {

                throw new OrganizationMismatchException(
                        "Manager belongs to another organization.");
            }

            team.setManager(manager);
        }

        Team savedTeam = teamRepository.save(team);
        
        teamWebSocketService.broadcastTeamEvent(
                TeamEventDTO.builder()
                        .eventType("CREATED")
                        .teamId(savedTeam.getId())
                        .organizationId(
                                savedTeam.getOrganization().getId()
                        )
                        .build()
        );
        sendOrganizationNotification(
                user,
                user.getFullName()
                        + " created a new team: "
                        + savedTeam.getName(),
                "TEAM_CREATED"
        );

        return mapToResponse(savedTeam);
    }

    @Override
    public TeamResponseDTO updateTeam(
            Long teamId,
            TeamRequestDTO request,
            String userEmail) {

        User user = getUser(userEmail);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Team not found"));

        // Ensure the team belongs to the same organization
        validateSameOrganization(user, team);
        validateTeamManagementAccess(user, team);

        team.setName(request.getName());
        team.setDescription(request.getDescription());

        // Update department
        if (request.getDepartmentId() != null) {

            team.setDepartment(
                    departmentRepository.findById(request.getDepartmentId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Department not found")));
        } else {
            team.setDepartment(null);
        }

        // Update manager
        if (request.getManagerId() != null) {

            User manager = userRepository.findById(
                    request.getManagerId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Manager not found"));

            if (!manager.getOrganization().getId()
                    .equals(user.getOrganization().getId())) {

                throw new OrganizationMismatchException(
                        "Manager belongs to another organization.");
            }

            team.setManager(manager);

        } else {

            team.setManager(null);
        }

        Team updatedTeam = teamRepository.save(team);
        
        teamWebSocketService.broadcastTeamEvent(
                TeamEventDTO.builder()
                        .eventType("UPDATED")
                        .teamId(updatedTeam.getId())
                        .organizationId(
                                updatedTeam.getOrganization().getId()
                        )
                        .build()
        );
        
        sendOrganizationNotification(
                user,
                user.getFullName()
                        + " updated team: "
                        + updatedTeam.getName(),
                "TEAM_UPDATED"
        );

        return mapToResponse(updatedTeam);
    }

    @Override
    public void deleteTeam(
            Long teamId,
            String userEmail) {

    	User user = getUser(userEmail);

    	Team team = teamRepository.findById(teamId)
    	        .orElseThrow(() ->
    	                new ResourceNotFoundException(
    	                        "Team not found"));

    	validateSameOrganization(user, team);
    	validateTeamManagementAccess(user, team);

    	String teamName = team.getName();
    	Long organizationId = team.getOrganization().getId();

    	teamRepository.delete(team);

    	teamWebSocketService.broadcastTeamEvent(
    	        TeamEventDTO.builder()
    	                .eventType("DELETED")
    	                .teamId(teamId)
    	                .organizationId(organizationId)
    	                .build()
    	);
    	
    	

    	sendOrganizationNotification(
    	        user,
    	        user.getFullName()
    	                + " deleted team: "
    	                + teamName,
    	        "TEAM_DELETED"
    	);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getOrganizationTeams(
            String userEmail) {

        User user = getUser(userEmail);

        if (user.getOrganization() == null) {
            throw new ResourceNotFoundException(
                    "User is not assigned to an organization."
            );
        }

        return teamRepository.findByOrganizationId(
                        user.getOrganization().getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

	@Override
	public TeamResponseDTO assignManager(
	        Long teamId,
	        Long managerId,
	        String userEmail) {

	    User user = getUser(userEmail);

	    validateOwnerOrManager(user);

	    Team team = teamRepository.findById(teamId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Team not found"));

	    validateSameOrganization(user, team);

	    User manager = userRepository.findById(managerId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Manager not found"));

	    if (manager.getOrganization() == null ||
	        !manager.getOrganization().getId()
	            .equals(team.getOrganization().getId())) {

	        throw new OrganizationMismatchException(
	                "Manager belongs to another organization.");
	    }

	    if (manager.getRole() != Role.MANAGER) {
	        throw new UnauthorizedTeamAccessException(
	                "Only users with MANAGER role can be assigned as a team manager."
	        );
	    }

	    if (team.getManager() == null ||
	        !team.getManager().getId().equals(managerId)) {

	        if (teamRepository.countByManager_Id(managerId) > 0) {

	            throw new UnauthorizedTeamAccessException(
	                    "This manager is already assigned to another team."
	            );
	        }
	    }

	    team.setManager(manager);

	    Team updatedTeam = teamRepository.save(team);
	    
	    teamWebSocketService.broadcastTeamEvent(
	            TeamEventDTO.builder()
	                    .eventType("MANAGER_ASSIGNED")
	                    .teamId(updatedTeam.getId())
	                    .organizationId(
	                            updatedTeam.getOrganization().getId()
	                    )
	                    .build()
	    );
	    sendOrganizationNotification(
	            user,
	            user.getFullName()
	                    + " assigned "
	                    + manager.getFullName()
	                    + " as manager of "
	                    + updatedTeam.getName(),
	            "MANAGER_ASSIGNED"
	    );

	    return mapToResponse(updatedTeam);
	}

	@Override
	public void assignEmployeeToTeam(
	        Long teamId,
	        Long employeeId,
	        String userEmail) {

	    User user = getUser(userEmail);

	    if (user.getRole() != Role.OWNER) {
	        throw new UnauthorizedTeamAccessException(
	                "Only the organization owner can add employees to teams."
	        );
	    }

	    Team team = teamRepository.findById(teamId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Team not found"));

	    validateSameOrganization(user, team);

	    User employee = userRepository.findById(employeeId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Employee not found"));

	    if (!employee.getOrganization().getId()
	            .equals(team.getOrganization().getId())) {

	        throw new OrganizationMismatchException(
	                "Employee belongs to another organization.");
	    }

	    if (teamMemberRepository.existsByTeamIdAndUserId(
	            teamId,
	            employeeId)) {

	        throw new EmployeeAlreadyAssignedException(
	                "Employee is already assigned to this team.");
	    }

	    TeamMember teamMember = TeamMember.builder()
	            .team(team)
	            .user(employee)
	            .build();

	    teamMemberRepository.save(teamMember);
	    
	    teamWebSocketService.broadcastTeamEvent(
	            TeamEventDTO.builder()
	                    .eventType("MEMBER_ADDED")
	                    .teamId(team.getId())
	                    .organizationId(
	                            team.getOrganization().getId()
	                    )
	                    .build()
	    );
	    
	    sendOrganizationNotification(
	            user,
	            user.getFullName()
	                    + " added "
	                    + employee.getFullName()
	                    + " to team "
	                    + team.getName(),
	            "EMPLOYEE_ADDED_TO_TEAM"
	    );

	    if (employee.getRole() == Role.MANAGER &&
	        team.getManager() == null) {

	        if (teamRepository.countByManager_Id(employeeId) == 0) {
	            team.setManager(employee);
	            teamRepository.save(team);
	        }
	    }
	}

	
	@Override
	public void removeEmployeeFromTeam(
	        Long teamId,
	        Long employeeId,
	        String userEmail) {

	    User user = getUser(userEmail);

	    // Authorization is checked after loading the team
	    Team team = teamRepository.findById(teamId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Team not found"));

	    validateSameOrganization(user, team);
	    validateTeamManagementAccess(user, team);

	    if (!teamMemberRepository.existsByTeamIdAndUserId(
	            teamId,
	            employeeId)) {

	        throw new ResourceNotFoundException(
	                "Employee is not assigned to this team.");
	    }

	    User employee = userRepository.findById(employeeId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "Employee not found"));

	    teamMemberRepository.deleteByTeamIdAndUserId(
	            teamId,
	            employeeId);
	    
	    teamWebSocketService.broadcastTeamEvent(
	            TeamEventDTO.builder()
	                    .eventType("MEMBER_REMOVED")
	                    .teamId(team.getId())
	                    .organizationId(
	                            team.getOrganization().getId()
	                    )
	                    .build()
	    );

	    sendOrganizationNotification(
	            user,
	            user.getFullName()
	                    + " removed "
	                    + employee.getFullName()
	                    + " from team "
	                    + team.getName(),
	            "EMPLOYEE_REMOVED_FROM_TEAM"
	    );
	}
	


	@Override
	@Transactional(readOnly = true)
	public List<TeamMemberResponseDTO> getTeamMembers(
	        Long teamId,
	        String userEmail) {

	    User loggedInUser = getUser(userEmail);

	    Team team = teamRepository.findById(teamId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Team not found"));

	    if (loggedInUser.getOrganization() == null) {
	        throw new ResourceNotFoundException(
	                "User is not assigned to an organization."
	        );
	    }

	    if (!team.getOrganization().getId()
	            .equals(loggedInUser.getOrganization().getId())) {

	        throw new RuntimeException(
	                "Unauthorized access."
	        );
	    }

	    return teamMemberRepository.findByTeamId(teamId)
	            .stream()
	            .map(this::mapToTeamMemberResponse)
	            .toList();
	}
	
	private User getUser(String email) {

	    return userRepository.findByEmail(email)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "User not found"
	                    )
	            );
	}
	
	private TeamResponseDTO mapToResponse(Team team) {

	    return TeamResponseDTO.builder()
	            .id(team.getId())
	            .name(team.getName())
	            .description(team.getDescription())
	            .organizationId(team.getOrganization().getId())
	            .departmentId(
	                    team.getDepartment() != null
	                            ? team.getDepartment().getId()
	                            : null
	            )
	            .managerId(
	                    team.getManager() != null
	                            ? team.getManager().getId()
	                            : null
	            )
	            .managerName(
	                    team.getManager() != null
	                            ? team.getManager().getFullName()
	                            : null
	            )
	            .createdAt(team.getCreatedAt())
	            .build();
	}
	
	private TeamMemberResponseDTO mapToTeamMemberResponse(
	        TeamMember teamMember
	) {

	    User user = teamMember.getUser();

	    return TeamMemberResponseDTO.builder()
	            .userId(user.getId())
	            .fullName(user.getFullName())
	            .email(user.getEmail())
	            .role(user.getRole())
	            .joinedAt(teamMember.getJoinedAt())
	            .build();
	}
	
	private void validateOwnerOrManager(User user) {

	    if (user.getRole() != Role.OWNER &&
	        user.getRole() != Role.MANAGER) {

	        throw new UnauthorizedTeamAccessException(
	                "Only OWNER or MANAGER can perform this action.");
	    }
	}
	
	private void validateTeamManagementAccess(
	        User user,
	        Team team) {

	    if (user.getRole() == Role.OWNER) {
	        return;
	    }

	    if (user.getRole() == Role.MANAGER &&
	        team.getManager() != null &&
	        team.getManager().getId().equals(user.getId())) {

	        return;
	    }

	    throw new UnauthorizedTeamAccessException(
	            "You can only manage your assigned team."
	    );
	}

	private void validateSameOrganization(User user, Team team) {

	    if (user.getOrganization() == null) {
	        throw new ResourceNotFoundException(
	                "User is not assigned to an organization."
	        );
	    }

	    if (team.getOrganization() == null ||
	        !team.getOrganization().getId()
	                .equals(user.getOrganization().getId())) {

	        throw new UnauthorizedTeamAccessException(
	                "Unauthorized access to this team."
	        );
	    }
	}
	
	
	private void sendOrganizationNotification(
	        User actor,
	        String message,
	        String type) {

	    List<User> organizationUsers =
	            userRepository.findByOrganization(
	                    actor.getOrganization()
	            );

	    for (User user : organizationUsers) {

	    	notificationService.sendNotification(
	    	        user,
	    	        actor,
	    	        message,
	    	        type
	    	);
	    }
	}

}