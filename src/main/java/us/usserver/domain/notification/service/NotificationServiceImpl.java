package us.usserver.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.notification.Notification;
import us.usserver.domain.notification.repository.NotificationRepository;
import us.usserver.domain.notification.dto.NotificationMessage;
import us.usserver.domain.notification.dto.NotificationType;
import us.usserver.global.EntityFacade;
import us.usserver.domain.notification.repository.EmitterRepository;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EntityFacade entityFacade;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public SseEmitter subscribe(Long receiverId, Long lastEventId) {
        Long emitterId = makeTimeIncludeId(receiverId);

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.delete(emitterId));
        emitter.onTimeout(() -> emitterRepository.delete(emitterId));
        emitter.onError((e) -> emitterRepository.delete(emitterId));

        // 503 에러 방지를 위한 더미 이벤트 전송
        Long eventId = makeTimeIncludeId(receiverId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [receiverId = " + receiverId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실 예방
        if (hasLostData(lastEventId)) {
            sendLostData(emitter, lastEventId, receiverId, emitterId);
        }
        return emitter;
    }

    @Override
    public Long makeTimeIncludeId(Long receiverId) {
        return receiverId + System.currentTimeMillis();
    }

    @Override
    public void sendNotification(SseEmitter emitter, Long eventId, Long emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(eventId))
                    .name("sse")
                    .data(data));
        } catch (IOException e) {
            emitterRepository.delete(emitterId);
            log.error("SSE 연결 오류");
        }
    }

    @Override
    public boolean hasLostData(Long lastEventId) {
        return lastEventId != null;
    }

    @Override
    public void sendLostData(SseEmitter emitter, Long lastEventId, Long receiverId, Long emitterId) {
        Map<Long, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByReceiverId(receiverId);
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    @Override
    @Transactional
    public void send(Set<Long> receiversId, String title, String content, String url, NotificationType notificationType) {
        receiversId.stream()
                .map(entityFacade::getAuthor)
                .map(receiver -> createNotification(receiver, title, content, notificationType, url))
                .forEach(notification -> {
                    Long receiverId = notification.getReceiver().getId();
                    Long eventId = makeTimeIncludeId(receiverId);
                    Map<Long, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByReceiverId(receiverId);

                    emitters.forEach((key, emitter) -> {
                        emitterRepository.saveEventCache(key, notification);
                        sendNotification(emitter, eventId, key, NotificationMessage.fromNotification(notification));
                    });
                });
    }

    private Notification createNotification(Author receiver, String title, String content, NotificationType notificationType, String url) {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .title(title)
                .content(content)
                .notificationType(notificationType)
                .isRead(false)
                .url(url)
                .build();

        return notificationRepository.save(notification);
    }
}
