package us.usserver.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    SseEmitter save(Long emitterId, SseEmitter sseEmitter);

    void saveEventCache(Long eventCacheId, Object event);

    Map<Long, SseEmitter> findAllEmitterStartWithByReceiverId(Long receiverId);

    Map<Long, Object> findAllEventCacheStartWithByReceiverId(Long receiverId);

    void delete(Long receiverId);

    void deleteAllEmitterStartWithId(Long receiverId);

    void deleteAllEventCacheStartWithId(Long receiverId);
}
