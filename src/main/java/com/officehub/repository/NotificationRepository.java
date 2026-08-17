package com.officehub.repository;

import com.officehub.entity.Notification;
import com.officehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    long countByUserAndReadFalse(User user);

    void deleteByUser(User user);

    List<Notification> findByUserAndReadFalse(User user);

    void deleteByCreatedAtBefore(LocalDateTime dateTime);

    Optional<Notification> findFirstByUserAndSenderAndTypeAndReadFalseOrderByCreatedAtDesc(
            User user,
            User sender,
            String type
    );
}