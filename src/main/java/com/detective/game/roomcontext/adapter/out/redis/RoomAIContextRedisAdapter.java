package com.detective.game.roomcontext.adapter.out.redis;


import com.detective.game.roomcontext.application.port.out.DeleteRoomContextPort;
import com.detective.game.roomcontext.application.port.out.LoadRoomContextPort;
import com.detective.game.roomcontext.application.port.out.SaveRoomContextPort;
import com.detective.game.roomcontext.domain.RoomAIContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class RoomAIContextRedisAdapter
        implements LoadRoomContextPort, SaveRoomContextPort, DeleteRoomContextPort {

    private final RoomAIContextRedisRepository repository;
    private final RoomAIContextMapper mapper;

    @Override
    public RoomAIContext load(String roomId) {
        return repository.findById(roomId)
                .map(mapper::toDomain)
                .orElseGet(() -> new RoomAIContext(roomId));
    }

    @Override
    public void save(RoomAIContext ctx) {
        RoomAIContextRedisEntity redisEntity = mapper.toRedis(ctx);
        repository.save(redisEntity); // TTL 자동 적용
    }

    @Override
    public void delete(String roomId) {
        repository.deleteById(roomId);
    }
}
