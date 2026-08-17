package com.officehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User who receives this notification.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * User who caused the notification.
     * Used for grouping chat notifications by sender.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    /**
     * Notification message shown in the UI.
     */
    @Column(nullable = false, length = 500)
    private String message;

    /**
     * Notification type.
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * Whether the notification has been read.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean read = false;

    /**
     * Creation timestamp.
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}