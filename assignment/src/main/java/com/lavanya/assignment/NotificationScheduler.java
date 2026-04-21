package com.lavanya.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class NotificationScheduler {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 300000) // runs every 5 minutes
    public void sweepPendingNotifications() {

        System.out.println("CRON: Sweeping pending notifications...");

        // Scan all keys matching user:*:pending_notifs
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys == null || keys.isEmpty()) {
            System.out.println("CRON: No pending notifications found.");
            return;
        }

        for (String key : keys) {
            // Pop all messages from the list
            List<String> messages = redisTemplate.opsForList().range(key, 0, -1);

            if (messages == null || messages.isEmpty()) {
                continue;
            }

            // Extract userId from key like "user:5:pending_notifs"
            String userId = key.split(":")[1];

            String firstName = messages.get(0);
            int othersCount = messages.size() - 1;

            if (othersCount > 0) {
                System.out.println("Summarized Push Notification for user " + userId
                        + ": " + firstName + " and " + othersCount + " others interacted with your posts.");
            } else {
                System.out.println("Summarized Push Notification for user " + userId
                        + ": " + firstName);
            }

            // Clear the list
            redisTemplate.delete(key);
        }
    }
}