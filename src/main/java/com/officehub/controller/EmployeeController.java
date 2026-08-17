package com.officehub.controller;

import jakarta.validation.Valid;
import com.officehub.dto.PendingInvitationDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.officehub.dto.InviteEmployeeRequestDTO;
import com.officehub.service.EmployeeService;

import java.util.List;

import com.officehub.dto.EmployeeResponseDTO;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/invite")
    public ResponseEntity<String> inviteEmployee(
            Authentication authentication,
            @Valid @RequestBody InviteEmployeeRequestDTO request) {

        employeeService.inviteEmployee(authentication.getName(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Invitation sent successfully.");
    }
    
    @PostMapping("/invitation/{invitationId}/accept")
    public ResponseEntity<String> acceptInvitation(
            @PathVariable Long invitationId,
            Authentication authentication) {

        employeeService.acceptInvitation(
                invitationId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Organization invitation accepted successfully."
        );
    }


    @PostMapping("/invitation/{invitationId}/reject")
    public ResponseEntity<String> rejectInvitation(
            @PathVariable Long invitationId,
            Authentication authentication) {

        employeeService.rejectInvitation(
                invitationId,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Organization invitation rejected successfully."
        );
    }
    
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getOrganizationMembers(
            Authentication authentication) {

        return ResponseEntity.ok(
                employeeService.getOrganizationMembers(authentication.getName()));
    }
    
    @GetMapping("/invitation")
    public ResponseEntity<PendingInvitationDTO> getPendingInvitation(
            Authentication authentication) {

        return employeeService
                .getPendingInvitation(authentication.getName())
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.noContent().build());
    }
    
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> removeEmployee(
            @PathVariable Long employeeId,
            Authentication authentication) {

        employeeService.removeEmployee(employeeId, authentication.getName());

        return ResponseEntity.ok("Employee removed successfully.");
    }
}