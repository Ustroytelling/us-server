package us.usserver.domain.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import us.usserver.domain.notification.dto.NotificationType;

import java.util.Set;

@Service
public interface NotificationService {
    SseEmitter subscribe(Long receiverId, Long lastEventId);

    Long makeTimeIncludeId(Long receiverId);

    void sendNotification(SseEmitter emitter, Long eventId, Long emitterId, Object data);

    boolean hasLostData(Long lastEventId);

    void sendLostData(SseEmitter emitter, Long lastEventId, Long memberId, Long emitterId);

    void send(Set<Long> receiversId, String title, String content, String url, NotificationType notificationType);
}
