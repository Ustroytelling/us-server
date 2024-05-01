package us.usserver.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.notification.Notification;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private Long receiverId;
    private String title;
    private String content;
    private String url;
    private NotificationType type;
    private String createdAt;

    public static NotificationMessage fromNotification(Notification notification) {
        return NotificationMessage.builder()
                .receiverId(notification.getReceiver().getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .url(notification.getUrl())
                .type(notification.getNotificationType())
                .createdAt(notification.getCreatedAt().toString())
                .build();
    }
}
