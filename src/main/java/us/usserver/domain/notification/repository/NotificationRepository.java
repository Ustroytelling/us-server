package us.usserver.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.usserver.domain.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
