# Spring Boot Backend Assignment

## Tech Stack
Java 17+, Spring Boot 3.x, PostgreSQL, Redis

## How to Run

### 1. Start PostgreSQL and Redis
```bash
docker-compose up -d
```

### 2. Run the Spring Boot app
```bash
./mvnw spring-boot:run
```

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/posts | Create a post |
| POST | /api/posts/{id}/comments | Add a comment |
| POST | /api/posts/{id}/like | Like a post |
| GET  | /api/posts/{id}/virality | Get virality score |

## How Thread Safety Works (Phase 2)

### Horizontal Cap (Max 100 bot replies)
Redis `INCR` command is atomic. Even if 200 requests
arrive at the same millisecond, Redis processes them
one at a time. When the count exceeds 100, we decrement
and reject the request with 429.

### Vertical Cap (Max depth 20)
Checked before saving. If depthLevel > 20, request
is rejected immediately.

### Cooldown Cap (Bot per human, 10 min)
A Redis key with TTL is set on first interaction.
If the key exists on next request, it is blocked.
Redis key expiry is handled server-side — no timers
in Java memory.

## All state is stored in Redis
No HashMaps or static variables are used.
PostgreSQL stores actual content.
Redis acts as the gatekeeper for all counters,
cooldowns, and notifications.