package com.lavanya.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void handleBotNotification(Long userId, String botName) {

        String cooldownKey = "notif_cooldown:user_" + userId;
        String pendingKey = "user:" + userId + ":pending_notifs";
        String message = botName + " replied to your post";

        Boolean hasCooldown = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(hasCooldown)) {
            // User already got a notification recently — queue it
            redisTemplate.opsForList().rightPush(pendingKey, message);
            System.out.println("Queued notification for user " + userId + ": " + message);
        } else {
            // No cooldown — send immediately and set 15 min cooldown
            System.out.println("Push Notification Sent to User " + userId + ": " + message);
            redisTemplate.opsForValue().set(cooldownKey, "1", 15, TimeUnit.MINUTES);
        }
    }
}