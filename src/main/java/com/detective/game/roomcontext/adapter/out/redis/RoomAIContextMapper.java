package com.detective.game.roomcontext.adapter.out.redis;

import com.detective.game.ai.domain.AIChatMessage;
import com.detective.game.roomcontext.domain.DoorState;
import com.detective.game.roomcontext.domain.NoteItem;
import com.detective.game.roomcontext.domain.RoomAIContext;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomAIContextMapper {

    // Redis → Domain
    default RoomAIContext toDomain(RoomAIContextRedisEntity e) {
        RoomAIContext ctx = new RoomAIContext(e.getRoomId());

        if (e.getSharedInventory() != null)
            e.getSharedInventory().forEach(i ->
                    ctx.addNoteItem(
                            NoteItem.of(i.getNoteName(), i.isHeSpy(), i.getItemId())
                    )
            );

        if (e.getDoorStates() != null)
            e.getDoorStates().values().forEach(d ->
                    ctx.updateDoorState(
                            DoorState.of(d.getDoorId(), d.isLocked())
                    )
            );

        if (e.getChatHistory() != null)
            e.getChatHistory().forEach(c ->
                    ctx.addChatMessage(
                            AIChatMessage.of(c.getRole(), c.getContent())
                    )
            );

        if (e.isFinalSubmitted())
            ctx.markFinalSubmitted();

        return ctx;
    }

    // Domain → Redis (직접 구현)
    default RoomAIContextRedisEntity toRedis(RoomAIContext ctx) {

        List<RoomAIContextRedisEntity.InventoryItemRedis> inventory = ctx.getSharedInventory().stream()
                .map(i -> new RoomAIContextRedisEntity.InventoryItemRedis(
                        i.getNoteName(),
                        i.isHeSpy(),
                        i.getItemId()
                ))
                .toList();

        Map<String, RoomAIContextRedisEntity.DoorStateRedis> doors =
                ctx.getDoorStateValues().stream()
                        .map(d -> new RoomAIContextRedisEntity.DoorStateRedis(
                                d.getDoorId(),
                                d.isLocked()
                        ))
                        .collect(Collectors.toMap(
                                RoomAIContextRedisEntity.DoorStateRedis::getDoorId,
                                x -> x
                        ));

        var chatHistory = ctx.getChatHistory().stream()
                .map(c -> new RoomAIContextRedisEntity.ChatMessageRedis(
                        c.getRole(),
                        c.getContent()
                ))
                .toList();

        return new RoomAIContextRedisEntity(
                ctx.getRoomId(),
                inventory,
                doors,
                chatHistory,
                ctx.isFinalSubmitted()
        );
    }
}