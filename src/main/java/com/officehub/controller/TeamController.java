package com.officehub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.officehub.dto.TeamMemberResponseDTO;
import com.officehub.dto.TeamRequestDTO;
import com.officehub.dto.TeamResponseDTO;
import com.officehub.service.TeamService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(
            @RequestBody TeamRequestDTO request,
            Authentication authentication) {

        return new ResponseEntity<>(
                teamService.createTeam(
                        request,
                        authentication.getName()),
                HttpStatus.CREATED);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamResponseDTO> updateTeam(
            @PathVariable Long teamId,
            @RequestBody TeamRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                teamService.updateTeam(
                        teamId,
                        request,
                        authentication.getName()));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(
            @PathVariable Long teamId,
            Authentication authentication) {

        teamService.deleteTeam(
                teamId,
                authentication.getName());

        return ResponseEntity.ok(
                "Team deleted successfully.");
    }

    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> getOrganizationTeams(
            Authentication authentication) {

        return ResponseEntity.ok(
                teamService.getOrganizationTeams(
                        authentication.getName()));
    }

    @PutMapping("/{teamId}/manager/{managerId}")
    public ResponseEntity<TeamResponseDTO> assignManager(
            @PathVariable Long teamId,
            @PathVariable Long managerId,
            Authentication authentication) {

        return ResponseEntity.ok(
                teamService.assignManager(
                        teamId,
                        managerId,
                        authentication.getName()));
    }

    @PostMapping("/{teamId}/members/{employeeId}")
    public ResponseEntity<String> assignEmployeeToTeam(
            @PathVariable Long teamId,
            @PathVariable Long employeeId,
            Authentication authentication) {

        teamService.assignEmployeeToTeam(
                teamId,
                employeeId,
                authentication.getName());

        return ResponseEntity.ok(
                "Employee assigned successfully.");
    }

    @DeleteMapping("/{teamId}/members/{employeeId}")
    public ResponseEntity<String> removeEmployeeFromTeam(
            @PathVariable Long teamId,
            @PathVariable Long employeeId,
            Authentication authentication) {

        teamService.removeEmployeeFromTeam(
                teamId,
                employeeId,
                authentication.getName());

        return ResponseEntity.ok(
                "Employee removed successfully.");
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberResponseDTO>> getTeamMembers(
            @PathVariable Long teamId,
            Authentication authentication) {

        return ResponseEntity.ok(
                teamService.getTeamMembers(
                        teamId,
                        authentication.getName()));
    }
}