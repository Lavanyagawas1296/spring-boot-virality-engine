package com.lavanya.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class BotGuardService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean isBotLimitReached(Long postId) {
        String key = "post:" + postId + ":bot_count";
        Long count = redisTemplate.opsForValue().increment(key, 1);
        if (count > 100) {
            redisTemplate.opsForValue().decrement(key);
            return true;
        }
        return false;
    }

    public boolean isDepthLimitReached(int depthLevel) {
        return depthLevel > 20;
    }

    public boolean isCooldownActive(Long botId, Long humanId) {
        String key = "cooldown:bot_" + botId + ":human_" + humanId;
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            return true;
        }

        redisTemplate.opsForValue().set(key, "1", 10, TimeUnit.MINUTES);
        return false; // allowed
    }
}