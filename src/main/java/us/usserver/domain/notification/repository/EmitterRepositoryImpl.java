package us.usserver.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository{
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<Long, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(Long emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(Long eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    @Override
    public Map<Long, SseEmitter> findAllEmitterStartWithByReceiverId(Long receiverId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().equals(receiverId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<Long, Object> findAllEventCacheStartWithByReceiverId(Long receiverId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().equals(receiverId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void delete(Long emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public void deleteAllEmitterStartWithId(Long receiverId) {
        emitters.forEach((key, emitter) -> {
            if (key.equals(receiverId)) {
                emitters.remove(key);
            }
        });
    }

    @Override
    public void deleteAllEventCacheStartWithId(Long receiverId) {
        eventCache.forEach((key, emitter) -> {
            if (key.equals(receiverId)) {
                eventCache.remove(key);
            }
        });
    }
}
