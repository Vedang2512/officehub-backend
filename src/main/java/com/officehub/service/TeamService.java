package com.officehub.service;

import java.util.List;
import com.officehub.dto.TeamMemberResponseDTO;
import com.officehub.dto.TeamRequestDTO;
import com.officehub.dto.TeamResponseDTO;


public interface TeamService {

    TeamResponseDTO createTeam(
            TeamRequestDTO request,
            String userEmail
    );

    TeamResponseDTO updateTeam(
            Long teamId,
            TeamRequestDTO request,
            String userEmail
    );

    void deleteTeam(
            Long teamId,
            String userEmail
    );

    List<TeamResponseDTO> getOrganizationTeams(
            String userEmail
    );

    TeamResponseDTO assignManager(
            Long teamId,
            Long managerId,
            String userEmail
    );

    void assignEmployeeToTeam(
            Long teamId,
            Long employeeId,
            String userEmail
    );

    void removeEmployeeFromTeam(
            Long teamId,
            Long employeeId,
            String userEmail
    );

    List<TeamMemberResponseDTO> getTeamMembers(
            Long teamId,
            String userEmail
    );
}