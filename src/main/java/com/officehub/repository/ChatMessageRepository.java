package com.officehub.repository;

import com.officehub.entity.ChatMessage;
import com.officehub.entity.MessageStatus;
import com.officehub.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Returns the complete conversation between two users,
     * ordered from oldest to newest.
     */
    @Query("""
            SELECT m
            FROM ChatMessage m
            WHERE
            (
                m.sender = :user1
                AND
                m.receiver = :user2
            )
            OR
            (
                m.sender = :user2
                AND
                m.receiver = :user1
            )
            ORDER BY m.sentAt ASC
            """)
    List<ChatMessage> findConversation(
            @Param("user1") User user1,
            @Param("user2") User user2
    );

    /**
     * Returns all unread messages for a user.
     */
    List<ChatMessage> findByReceiverAndStatus(
            User receiver,
            MessageStatus status
    );

    /**
     * Returns all messages belonging to an organization.
     * Useful for admin tools, auditing, and future analytics.
     */
    List<ChatMessage> findByOrganizationIdOrderBySentAtAsc(
            Long organizationId
    );
    
    void deleteBySender(User user);

    void deleteByReceiver(User user);
    void deleteByOrganizationId(Long organizationId);
    
    @Query("""
            SELECT m.sender.id, COUNT(m.id)
            FROM ChatMessage m
            WHERE m.receiver = :receiver
            AND m.status = :status
            GROUP BY m.sender.id
            """)
    List<Object[]> countUnreadMessagesBySender(
            @Param("receiver") User receiver,
            @Param("status") MessageStatus status
    );

}