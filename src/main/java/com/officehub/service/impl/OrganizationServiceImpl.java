package com.officehub.service.impl;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.officehub.dto.UpdateOrganizationRequestDTO;
import com.officehub.dto.OrganizationMemberDTO;
import com.officehub.repository.TeamMemberRepository;
import com.officehub.repository.TeamRepository;
import com.officehub.repository.InvitationRepository;
import org.springframework.stereotype.Service;
import com.officehub.repository.TaskRepository;
import com.officehub.dto.CreateOrganizationRequestDTO;
import com.officehub.dto.OrganizationResponseDTO;
import com.officehub.entity.Organization;
import com.officehub.entity.Role;
import com.officehub.entity.Team;
import com.officehub.entity.User;
import com.officehub.exception.OrganizationAlreadyExistsException;
import com.officehub.exception.UnauthorizedActionException;
import com.officehub.repository.OrganizationRepository;
import com.officehub.repository.UserRepository;
import com.officehub.service.OrganizationService;
import com.officehub.repository.ChatMessageRepository;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final InvitationRepository invitationRepository;
    private final ChatMessageRepository chatMessageRepository;

    public OrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            TeamMemberRepository teamMemberRepository,
            TeamRepository teamRepository,
            TaskRepository taskRepository,
            InvitationRepository invitationRepository,
            ChatMessageRepository chatMessageRepository) {

        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.invitationRepository = invitationRepository;
        this.chatMessageRepository = chatMessageRepository;
    }


    @Override
    public OrganizationResponseDTO createOrganization(
            CreateOrganizationRequestDTO request,
            String email) {


        organizationRepository.findByOrganizationName(request.getOrganizationName())
                .ifPresent(org -> {
                    throw new OrganizationAlreadyExistsException(
                            "Organization with this name already exists.");
                });

        System.out.println("Logged in user email: " + email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> 
                    new RuntimeException("User not found"));
        
        if (user.getRole() != Role.OWNER) {
            throw new UnauthorizedActionException(
                "Only organization owners can create an organization"
            );
        }
        
        


        Organization organization = new Organization();

        organization.setOrganizationName(request.getOrganizationName());
        organization.setDescription(request.getDescription());
        organization.setCreatedAt(LocalDateTime.now());


        Organization savedOrganization =
                organizationRepository.save(organization);
        

        // Assign creator as organization owner
        user.setOrganization(savedOrganization);
        

        userRepository.save(user);


        return new OrganizationResponseDTO(
                savedOrganization.getId(),
                savedOrganization.getOrganizationName(),
                savedOrganization.getDescription(),
                savedOrganization.getCreatedAt());
    }
    
    @Override
    public OrganizationResponseDTO getMyOrganization(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );


        Organization organization = user.getOrganization();


        if (organization == null) {
            throw new RuntimeException(
                    "No organization found for user"
            );
        }


        return new OrganizationResponseDTO(
                organization.getId(),
                organization.getOrganizationName(),
                organization.getDescription(),
                organization.getCreatedAt()
        );
    }
    
    @Override
    @Transactional
    public void leaveOrganization(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (user.getOrganization() == null) {
            throw new RuntimeException(
                    "You are not part of any organization"
            );
        }

        if (user.getRole() == Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Organization owners cannot leave their organization. Delete the organization instead."
            );
        }

        // Remove user from all teams
        teamMemberRepository.deleteByUser(user);

        // Remove manager assignment if this user manages a team
        teamRepository.clearManager(user);

        // Remove user from organization
        user.setOrganization(null);

        userRepository.save(user);
    }
    
    @Override
    public List<OrganizationMemberDTO> getOrganizationMembers(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Organization organization = user.getOrganization();

        if (organization == null) {
            throw new RuntimeException(
                    "You are not part of any organization"
            );
        }

        return userRepository.findByOrganization(organization)
                .stream()
                .map(member -> new OrganizationMemberDTO(
                        member.getId(),
                        member.getFullName(),
                        member.getEmail(),
                        member.getRole().name(),
                        member.getDesignation(),
                        member.getProfileImage()
                ))
                .toList();
    }
    
    @Override
    public OrganizationResponseDTO updateOrganization(
            UpdateOrganizationRequestDTO request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (user.getRole() != Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Only organization owners can update the organization"
            );
        }

        Organization organization = user.getOrganization();

        if (organization == null) {
            throw new RuntimeException(
                    "You are not part of any organization"
            );
        }

        organizationRepository.findByOrganizationName(
                request.getOrganizationName()
        ).ifPresent(existingOrganization -> {

            if (!existingOrganization.getId()
                    .equals(organization.getId())) {

                throw new OrganizationAlreadyExistsException(
                        "Organization with this name already exists."
                );
            }
        });

        organization.setOrganizationName(
                request.getOrganizationName()
        );

        organization.setDescription(
                request.getDescription()
        );

        Organization updatedOrganization =
                organizationRepository.save(organization);

        return new OrganizationResponseDTO(
                updatedOrganization.getId(),
                updatedOrganization.getOrganizationName(),
                updatedOrganization.getDescription(),
                updatedOrganization.getCreatedAt()
        );
    }
    
    @Override
    @Transactional
    public void deleteOrganization(String email) {

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Only organization owners can delete the organization"
            );
        }

        Organization organization = owner.getOrganization();

        if (organization == null) {
            throw new RuntimeException(
                    "You are not part of any organization"
            );
        }

        Long organizationId = organization.getId();

        // Get all organization members
        List<User> members =
                userRepository.findByOrganizationId(organizationId);

        // Remove all members from their teams
        for (User member : members) {
            teamMemberRepository.deleteByUser(member);
        }

        // Remove manager assignments
        for (User member : members) {
            teamRepository.clearManager(member);
        }

        // Remove organization from all users
        for (User member : members) {
            member.setOrganization(null);
            userRepository.save(member);
        }

        // Delete all teams belonging to organization
        List<Team> teams =
                teamRepository.findByOrganizationId(organizationId);

        for (Team team : teams) {

            taskRepository.deleteByTeamId(team.getId());

        }

        teamRepository.deleteAll(teams);
        
        invitationRepository.deleteByOrganization(organization);
        chatMessageRepository.deleteByOrganizationId(organizationId);

        // Finally delete organization
        organizationRepository.delete(organization);
    }
}