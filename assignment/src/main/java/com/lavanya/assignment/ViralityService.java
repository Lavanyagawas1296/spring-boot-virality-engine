package com.lavanya.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class ViralityService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Call this whenever a bot replies to a post
    public void addBotReply(Long postId) {
        String key = "post:" + postId + ":virality_score";
        redisTemplate.opsForValue().increment(key, 1);
    }

    // Call this whenever a human likes a post
    public void addHumanLike(Long postId) {
        String key = "post:" + postId + ":virality_score";
        redisTemplate.opsForValue().increment(key, 20);
    }

    // Call this whenever a human comments on a post
    public void addHumanComment(Long postId) {
        String key = "post:" + postId + ":virality_score";
        redisTemplate.opsForValue().increment(key, 50);
    }

    // Get current virality score for a post
    public String getViralityScore(Long postId) {
        String key = "post:" + postId + ":virality_score";
        String score = redisTemplate.opsForValue().get(key);
        return score == null ? "0" : score;
    }
}