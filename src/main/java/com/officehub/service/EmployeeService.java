package com.officehub.service;

import java.util.List;


import com.officehub.dto.EmployeeResponseDTO;
import com.officehub.dto.InviteEmployeeRequestDTO;
import com.officehub.dto.PendingInvitationDTO;

import java.util.Optional;


public interface EmployeeService {

    void inviteEmployee(String ownerEmail, InviteEmployeeRequestDTO request);

    void acceptInvitation(Long invitationId, String employeeEmail);

    void rejectInvitation(Long invitationId, String employeeEmail);

    List<EmployeeResponseDTO> getOrganizationMembers(String ownerEmail);

    void removeEmployee(Long employeeId, String ownerEmail);
    Optional<PendingInvitationDTO> getPendingInvitation(
            String employeeEmail
    );
}