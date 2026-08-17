package com.officehub.service.impl;

import org.springframework.stereotype.Service;
import com.officehub.entity.Role;
import com.officehub.exception.ResourceNotFoundException;
import com.officehub.exception.UnauthorizedActionException;
import org.springframework.transaction.annotation.Transactional;

import com.officehub.entity.User;
import com.officehub.repository.UserRepository;
import com.officehub.repository.TaskRepository;
import com.officehub.repository.ChatMessageRepository;
import com.officehub.repository.NotificationRepository;
import com.officehub.repository.TeamMemberRepository;
import com.officehub.repository.TeamRepository;
import com.officehub.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationRepository notificationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;


    public UserServiceImpl(
            UserRepository userRepository,
            TaskRepository taskRepository,
            ChatMessageRepository chatMessageRepository,
            NotificationRepository notificationRepository,
            TeamMemberRepository teamMemberRepository,
            TeamRepository teamRepository
    ) {

        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.notificationRepository = notificationRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
    }


    @Override
    @Transactional
    public void deleteCurrentUser(String email) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // Remove user from team memberships
        teamMemberRepository.deleteByUser(user);


        // Remove manager reference from teams
        teamRepository.clearManager(user);


        // Remove notifications
        notificationRepository.deleteByUser(user);


        // Remove chat messages
        chatMessageRepository.deleteBySender(user);

        chatMessageRepository.deleteByReceiver(user);


        // Remove tasks assigned to the user
        taskRepository.deleteByAssignedTo(user);

        // Remove tasks created/assigned by the user
        taskRepository.deleteByAssignedBy(user);


        // Finally delete the user
        userRepository.delete(user);
    }
    
    @Override
    @Transactional
    public void assignManager(
            Long userId,
            String ownerEmail
    ) {

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found"
                        )
                );

        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Only organization owners can assign managers."
            );
        }

        User employee = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        if (employee.getOrganization() == null ||
            owner.getOrganization() == null ||
            !employee.getOrganization().getId()
                    .equals(owner.getOrganization().getId())) {

            throw new UnauthorizedActionException(
                    "User does not belong to your organization."
            );
        }

        if (employee.getRole() == Role.OWNER) {
            throw new UnauthorizedActionException(
                    "Owner cannot be assigned as manager."
            );
        }

        employee.setRole(Role.MANAGER);

        userRepository.save(employee);
    }

}