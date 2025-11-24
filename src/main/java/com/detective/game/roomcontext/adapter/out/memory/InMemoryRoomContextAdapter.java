package com.detective.game.roomcontext.adapter.out.memory;

import com.detective.game.roomcontext.application.port.out.LoadRoomContextPort;
import com.detective.game.roomcontext.application.port.out.SaveRoomContextPort;
import com.detective.game.roomcontext.domain.RoomAIContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InMemoryRoomContextAdapter implements LoadRoomContextPort, SaveRoomContextPort {
    private final ConcurrentMap<String, RoomAIContext> store = new ConcurrentHashMap<>();

    @Override
    public RoomAIContext load(String roomId) {
        return store.computeIfAbsent(roomId, RoomAIContext::new);
    }

    @Override
    public void save(RoomAIContext context) {
        store.put(context.getRoomId(), context);
    }
}
