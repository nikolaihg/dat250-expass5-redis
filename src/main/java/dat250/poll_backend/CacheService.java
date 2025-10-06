package dat250.poll_backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Redis cache for poll vote counts using atomic HINCRBY updates.
 *
 * Key format: poll:{pollId}:counts (Redis HASH)
 * TTL = 60 seconds.
 */
@Service
public class CacheService {
    private final UnifiedJedis jedis;
    private final int ttlSeconds;

    public CacheService(UnifiedJedis jedis, @Value("${redis.cache.ttl:60}") int ttlSeconds) {
        this.jedis = jedis;
        this.ttlSeconds = ttlSeconds;
    }

    private String key(UUID pollId) {
        return "poll:" + pollId + ":counts";
    }

    // Retrieve cached counts as a Map<Integer, Long>
    public Map<Integer, Long> get(UUID pollId) {
        Map<String, String> map = jedis.hgetAll(key(pollId));
        if (map == null || map.isEmpty()) return null;
        Map<Integer, Long> result = new HashMap<>();
        map.forEach((k, v) -> result.put(Integer.parseInt(k), Long.parseLong(v)));
        return result;
    }

    public void set(UUID pollId, Map<Integer, Long> counts) {
        String k = key(pollId);
        Map<String, String> data = new HashMap<>();
        counts.forEach((order, value) -> data.put(String.valueOf(order), String.valueOf(value)));
        jedis.del(k);
        jedis.hset(k, data);
        jedis.expire(k, ttlSeconds);
    }

    public void increment(UUID pollId, int presentationOrder) {
        String k = key(pollId);
        jedis.hincrBy(k, String.valueOf(presentationOrder), 1);
        jedis.expire(k, ttlSeconds); // refresh TTL on change
    }

    public void decrement(UUID pollId, int presentationOrder) {
        String k = key(pollId);
        jedis.hincrBy(k, String.valueOf(presentationOrder), -1);
        jedis.expire(k, ttlSeconds);
    }

    public void invalidate(UUID pollId) {
        jedis.del(key(pollId));
    }

}
