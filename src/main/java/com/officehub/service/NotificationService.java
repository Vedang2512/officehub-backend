
package com.officehub.service;

import com.officehub.dto.NotificationDTO;
import com.officehub.entity.User;

import java.util.List;

public interface NotificationService {

    /**
     * Create, save and immediately deliver
     * a notification to the user.
     */
	NotificationDTO sendNotification(
	        User user,
	        User sender,
	        String message,
	        String type
	);

    /**
     * Create, save and immediately deliver
     * a notification to all users in an organization.
     */
    void sendOrganizationNotification(
            Long organizationId,
            String message,
            String type,
            Long excludeUserId
    );

    /**
     * Get all notifications for a user.
     */
    List<NotificationDTO> getUserNotifications(User user);

    /**
     * Number of unread notifications.
     */
    long getUnreadCount(User user);

    /**
     * Mark one notification as read.
     */
    void markAsRead(
            Long notificationId,
            User user
    );
}

