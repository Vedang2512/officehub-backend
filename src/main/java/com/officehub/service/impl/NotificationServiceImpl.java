
package com.officehub.service.impl;

import com.officehub.dto.NotificationDTO;
import com.officehub.entity.Notification;
import com.officehub.entity.Organization;
import com.officehub.entity.User;
import com.officehub.repository.NotificationRepository;
import com.officehub.repository.OrganizationRepository;
import com.officehub.repository.UserRepository;
import com.officehub.service.NotificationService;
import com.officehub.websocket.WebSocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketService webSocketService;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            WebSocketService webSocketService,
            UserRepository userRepository,
            OrganizationRepository organizationRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.webSocketService = webSocketService;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public NotificationDTO sendNotification(
            User user,
            User sender,
            String message,
            String type
    ) {

        // CHAT notifications from the same sender are merged
        if ("CHAT".equals(type) && sender != null) {

            var existingNotification =
                    notificationRepository
                            .findFirstByUserAndSenderAndTypeAndReadFalseOrderByCreatedAtDesc(
                                    user,
                                    sender,
                                    "CHAT"
                            );

            if (existingNotification.isPresent()) {

                Notification notification =
                        existingNotification.get();

                String currentMessage =
                        notification.getMessage();

                int count = 1;

                if (currentMessage != null) {

                    java.util.regex.Matcher matcher =
                            java.util.regex.Pattern
                                    .compile("(\\d+) new messages")
                                    .matcher(currentMessage);

                    if (matcher.find()) {

                        count =
                                Integer.parseInt(matcher.group(1)) + 1;

                    } else {

                        count = 2;

                    }
                }

                notification.setMessage(
                        sender.getFullName()
                                + " sent you "
                                + count
                                + " new messages"
                );

                notification =
                        notificationRepository.save(notification);

                NotificationDTO dto =
                        mapToDTO(notification);

                webSocketService.sendToUser(
                        user.getEmail(),
                        "/queue/notifications",
                        dto
                );

                return dto;
            }
        }

        // Create a new notification
        Notification notification =
                Notification.builder()
                        .user(user)
                        .sender(sender)
                        .message(message)
                        .type(type)
                        .build();

        notification =
                notificationRepository.save(notification);

        NotificationDTO dto =
                mapToDTO(notification);

        webSocketService.sendToUser(
                user.getEmail(),
                "/queue/notifications",
                dto
        );

        return dto;
    }

    @Override
    public void sendOrganizationNotification(
            Long organizationId,
            String message,
            String type,
            Long excludeUserId
    ) {

        Organization organization =
                organizationRepository.findById(organizationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Organization not found"
                                )
                        );

        List<User> users =
                userRepository.findByOrganization(organization);

        for (User user : users) {

            if (excludeUserId != null &&
                    user.getId().equals(excludeUserId)) {
                continue;
            }

            sendNotification(
                    user,
                    null,
                    message,
                    type
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(User user) {

        LocalDateTime cutoff =
                LocalDateTime.now().minusDays(7);

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(notification ->
                        notification.getCreatedAt() != null &&
                        notification.getCreatedAt().isAfter(cutoff)
                )
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Override
    public void markAsRead(
            Long notificationId,
            User user
    ) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to modify this notification"
            );
        }

        notification.setRead(true);

        notificationRepository.save(notification);
    }
    
    
    private NotificationDTO mapToDTO(
            Notification notification
    ) {

        return NotificationDTO.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
    
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteNotificationsOlderThan7Days() {

        LocalDateTime cutoff =
                LocalDateTime.now().minusDays(7);

        notificationRepository.deleteByCreatedAtBefore(cutoff);
    }
}

