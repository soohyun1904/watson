package com.detective.game.roomcontext.adapter.out.redis;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "room_ai_context", timeToLive = 1800) // 30분 TTL
public class RoomAIContextRedisEntity {
    @Id
    private String roomId;

    private List<InventoryItemRedis> sharedInventory;

    private Map<String, DoorStateRedis> doorStates;

    private List<ChatMessageRedis> chatHistory;

    private boolean finalSubmitted;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryItemRedis {
        private String noteName;
        private boolean heSpy;
        private String itemId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoorStateRedis {
        private String doorId;
        private boolean locked;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageRedis {
        private String role;
        private String content;
    }
}
