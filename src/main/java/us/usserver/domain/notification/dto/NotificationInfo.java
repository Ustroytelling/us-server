package us.usserver.domain.notification.dto;

import lombok.*;

import java.util.Set;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationInfo {
    private Set<Long> receiversId;
    private String title;
    private String content;
    private NotificationType type;
    private String url;
}
