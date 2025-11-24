package com.detective.game.roomcontext.domain;

import com.detective.game.ai.domain.AIChatMessage;
import lombok.Getter;

import java.util.*;

@Getter
public class RoomAIContext {

    private final String roomId;

    // 플레이어들이 공유하는 인벤토리 (수집한 증거들)
    private final List<NoteItem> sharedInventory = new ArrayList<>();

    // 문 상태 (doorId 기준으로 관리)
    private final Map<String, DoorState> doorStates = new HashMap<>();

    // 채팅 히스토리 추가
    private final List<AIChatMessage> chatHistory = new ArrayList<>();

    private boolean finalSubmitted = false;

    public RoomAIContext(String roomId) {
        this.roomId = roomId;
    }

    // --- 증거 관련 ---
    public void addNoteItem(NoteItem item) {
        boolean exists = sharedInventory.stream()
                .anyMatch(i -> Objects.equals(i.getItemId(), item.getItemId()));

        if (!exists) {
            sharedInventory.add(item);
        }
    }

    public List<String> getCollectedClueCodes() {
        return sharedInventory.stream()
                .map(NoteItem::getNoteName)
                .distinct()
                .toList();
    }

    // --- 문 상태 관련 ---
    public void updateDoorState(DoorState state) {
        doorStates.put(state.getDoorId(), state);
    }

    public boolean isDoorLocked(String doorId) {
        DoorState ds = doorStates.get(doorId);
        return ds != null && ds.isLocked();
    }

    public Collection<DoorState> getDoorStateValues() {
        return doorStates.values();
    }

    // --- 채팅 히스토리 관련 ---
    public void addChatMessage(AIChatMessage message) {
        this.chatHistory.add(message);
    }

    public List<String> getChatHistoryAsStrings() {
        return chatHistory.stream()
                .map(AIChatMessage::getContent)
                .toList();
    }

    // --- 최종 제출 관련 ---
    public void markFinalSubmitted() {
        this.finalSubmitted = true;
    }
}