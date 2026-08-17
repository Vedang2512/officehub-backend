package com.officehub.service.impl;

import java.util.List;

import com.officehub.service.NotificationService;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.officehub.dto.EmployeeResponseDTO;
import com.officehub.dto.InviteEmployeeRequestDTO;
import com.officehub.dto.PendingInvitationDTO;
import com.officehub.repository.InvitationRepository;
import com.officehub.repository.OrganizationRepository;
import com.officehub.repository.UserRepository;
import com.officehub.service.EmployeeService;

import java.time.LocalDateTime;

import com.officehub.entity.Invitation;
import com.officehub.entity.InvitationStatus;
import com.officehub.entity.Organization;
import com.officehub.entity.Role;
import com.officehub.entity.User;
import com.officehub.exception.DuplicateInvitationException;
import com.officehub.exception.ResourceNotFoundException;
import com.officehub.exception.UnauthorizedActionException;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final InvitationRepository invitationRepository;
    private final NotificationService notificationService;

    public EmployeeServiceImpl(UserRepository userRepository,
            OrganizationRepository organizationRepository,
            InvitationRepository invitationRepository,
            NotificationService notificationService) {
		this.userRepository = userRepository;
		this.organizationRepository = organizationRepository;
		this.invitationRepository = invitationRepository;
		this.notificationService = notificationService;
	}

    @Override
    public void inviteEmployee(String ownerEmail, InviteEmployeeRequestDTO request) {

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Owner not found"));

        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Only organization owners can invite employees");
        }

        Organization organization = owner.getOrganization();

        if (organization == null) {
            throw new ResourceNotFoundException(
                    "Owner is not associated with any organization");
        }

        if (invitationRepository.existsByEmailAndOrganizationAndStatus(
                request.getEmail(),
                organization,
                InvitationStatus.PENDING)) {

            throw new DuplicateInvitationException(
                    "Employee has already been invited");
        }

        Invitation invitation = new Invitation();

        invitation.setEmail(request.getEmail());
        invitation.setOrganization(organization);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setInvitedAt(LocalDateTime.now());

        invitationRepository.save(invitation);
    }

    @Override
    public void acceptInvitation(
            Long invitationId,
            String employeeEmail) {

        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invitation not found"));

        // Make sure this invitation belongs to the logged-in employee
        if (!invitation.getEmail().equalsIgnoreCase(employeeEmail)) {
            throw new UnauthorizedActionException(
                    "This invitation does not belong to you");
        }

        // Invitation must still be pending
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new UnauthorizedActionException(
                    "This invitation has already been processed");
        }

        // Employee must not already belong to an organization
        if (employee.getOrganization() != null) {
            throw new UnauthorizedActionException(
                    "You are already part of an organization");
        }

        // Check invitation expiration (7 days)
        if (invitation.getInvitedAt()
                .plusDays(7)
                .isBefore(LocalDateTime.now())) {

            throw new UnauthorizedActionException(
                    "This invitation has expired");
        }

        employee.setOrganization(invitation.getOrganization());

        employee.setRole(Role.EMPLOYEE);

        invitation.setStatus(InvitationStatus.ACCEPTED);

        userRepository.save(employee);
        invitationRepository.save(invitation);

        List<User> organizationUsers =
                userRepository.findByOrganization(
                        invitation.getOrganization()
                );

        for (User organizationUser : organizationUsers) {

            if (organizationUser.getRole() == Role.OWNER ||
                organizationUser.getRole() == Role.MANAGER) {

                notificationService.sendNotification(
                        organizationUser,
                        null,
                        employee.getFullName()
                                + " has joined the organization.",
                        "EMPLOYEE_JOINED"
                );
            }
        }
    }
    
    @Override
    public void rejectInvitation(
            Long invitationId,
            String employeeEmail) {

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invitation not found"));

        // Make sure this invitation belongs to the logged-in employee
        if (!invitation.getEmail().equalsIgnoreCase(employeeEmail)) {
            throw new UnauthorizedActionException(
                    "This invitation does not belong to you");
        }

        // Invitation must still be pending
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new UnauthorizedActionException(
                    "This invitation has already been processed");
        }

        // Check invitation expiration (7 days)
        if (invitation.getInvitedAt()
                .plusDays(7)
                .isBefore(LocalDateTime.now())) {

            throw new UnauthorizedActionException(
                    "This invitation has expired");
        }

        invitation.setStatus(InvitationStatus.REJECTED);

        invitationRepository.save(invitation);
    }

    @Override
    public List<EmployeeResponseDTO> getOrganizationMembers(String email) {


        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));



        Organization organization =
                currentUser.getOrganization();



        if (organization == null) {

            throw new ResourceNotFoundException(
                    "User is not assigned to an organization"
            );

        }



        List<User> users =
                userRepository.findByOrganization(organization);



        return users.stream()

                .filter(user ->
                        !user.getId().equals(currentUser.getId())
                )

                .map(user ->
                        new EmployeeResponseDTO(
                                user.getId(),
                                user.getFullName(),
                                user.getEmail(),
                                user.getRole(),
                                user.getProfileImage(),
                                user.getDesignation()
                        )
                )

                .toList();

    }

    @Override
    public void removeEmployee(Long employeeId, String ownerEmail) {

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Owner not found"));

        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Only organization owners can remove employees");
        }

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        // Prevent removing the owner
        if (employee.getRole() == Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Owner cannot be removed");
        }

        // Ensure employee belongs to the same organization
        if (employee.getOrganization() == null ||
            !employee.getOrganization().getId().equals(owner.getOrganization().getId())) {

            throw new UnauthorizedActionException(
                    "Employee does not belong to your organization");
        }

        employee.setOrganization(null);

        userRepository.save(employee);
    }
    
    @Override
    public Optional<PendingInvitationDTO> getPendingInvitation(
            String employeeEmail) {

        return invitationRepository
                .findByEmailAndStatus(
                        employeeEmail,
                        InvitationStatus.PENDING
                )
                .map(invitation ->
                        new PendingInvitationDTO(
                                invitation.getId(),
                                invitation.getOrganization()
                                        .getOrganizationName(),
                                invitation.getOrganization()
                                        .getDescription()
                        )
                );
    }
}