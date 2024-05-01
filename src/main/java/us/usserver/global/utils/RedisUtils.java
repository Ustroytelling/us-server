package us.usserver.global.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisUtils {
    private final RedisTemplate<String, Long> redisTemplate;
    private final RedisTemplate<Long, String> redisSearchLogTemplate;

    private static final int SEARCH_LOG_SIZE = 10;
    private static final long KEYWORD_RANKING_KEY = 0L;


    public Long getData(String key) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setDate(String key, Long value) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public void setDateWithExpiration(String key, Long value, Duration duration) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, duration);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public void saveSearchLog(String keyword, Long memberId) {
        ListOperations<Long, String> listOperations = redisSearchLogTemplate.opsForList();
        ZSetOperations<Long, String> zSetOperations = redisSearchLogTemplate.opsForZSet();

        List<String> currentLogs = listOperations.range(memberId, 0, SEARCH_LOG_SIZE);
        if (currentLogs != null) {
            for (String existingLog : currentLogs) {
                if (existingLog.equals(keyword)) {
                    listOperations.remove(memberId, 1, existingLog);
                    break;
                }
            }
        }

        Long size = redisSearchLogTemplate.opsForList().size(memberId);
        if (size != null && size == SEARCH_LOG_SIZE) {
            listOperations.rightPop(memberId);
        }

        if (zSetOperations.addIfAbsent(KEYWORD_RANKING_KEY, keyword, 1) == Boolean.FALSE) {
            zSetOperations.incrementScore(KEYWORD_RANKING_KEY, keyword, 1);
        }
        listOperations.leftPush(memberId, keyword);
    }

    public List<String> getSearchLogs(Long memberId) {
        return redisSearchLogTemplate.opsForList().range(memberId, 0, SEARCH_LOG_SIZE);
    }

    public void deleteSearchLog(String keyword, Long memberId) {
        redisSearchLogTemplate.opsForList().remove(memberId, 1, keyword);
    }

    public List<String> getKeywordRanking() {
        Set<String> keywords = redisSearchLogTemplate.opsForZSet()
                .reverseRange(KEYWORD_RANKING_KEY, 0, SEARCH_LOG_SIZE - 1);
        if (keywords == null || keywords.isEmpty()) {
            return Collections.emptyList();
        } else {
            return keywords.stream().toList();
        }
    }
}
