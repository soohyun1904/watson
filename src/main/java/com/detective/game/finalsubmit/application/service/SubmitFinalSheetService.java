package com.detective.game.finalsubmit.application.service;

import com.detective.game.ai.application.port.command.EvaluateFinalSubmitCommand;
import com.detective.game.ai.application.port.out.EvaluateFinalSubmitPort;
import com.detective.game.ai.application.port.out.dto.AIEvaluateRawResponse;
import com.detective.game.ai.domain.AIResult;
import com.detective.game.clue.application.port.out.LoadCluePort;
import com.detective.game.clue.domain.Clue;
import com.detective.game.common.exception.FinalSubmitException;
import com.detective.game.finalsubmit.adapter.out.persistence.FinalSubmitJpaEntity;
import com.detective.game.finalsubmit.adapter.out.persistence.FinalSubmitRepository;
import com.detective.game.finalsubmit.application.port.comand.SubmitFinalSheetCommand;
import com.detective.game.finalsubmit.application.port.in.SubmitFinalSheetUseCase;
import com.detective.game.finalsubmit.domain.FinalSheet;
import com.detective.game.roomcontext.application.port.out.DeleteRoomContextPort;
import com.detective.game.roomcontext.application.port.out.LoadRoomContextPort;
import com.detective.game.roomcontext.application.port.out.SaveRoomContextPort;
import com.detective.game.roomcontext.domain.RoomAIContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.detective.game.common.exception.ErrorMessage.ROOM_ALREADY_FINAL_SUBMITTED;

@Service
@RequiredArgsConstructor
public class SubmitFinalSheetService implements SubmitFinalSheetUseCase {

    private final FinalSubmitRepository repository;
    private final LoadRoomContextPort loadPort;
    private final SaveRoomContextPort savePort;
    private final DeleteRoomContextPort deletePort;
    private final LoadCluePort loadCluePort;
    private final EvaluateFinalSubmitPort evaluateFinalSubmitPort;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void submit(SubmitFinalSheetCommand command) {

        // 1) Room 컨텍스트 로드
        RoomAIContext ctx = loadPort.load(command.roomId());


        // 이미 제출했다면 예외 처리
        if (ctx.isFinalSubmitted()) {
            throw new FinalSubmitException(ROOM_ALREADY_FINAL_SUBMITTED);
        }

        // 2) FinalSheet 도메인 생성
        FinalSheet sheet = FinalSheet.of(
                command.isFinal(),
                command.answers()
        );

        // 3) 단서 코드 → Clue 조회
        List<String> codes = ctx.getCollectedClueCodes();
        List<Clue> clues = codes.isEmpty()
                ? List.of()
                : loadCluePort.loadByCodes(codes);

        // 4) 평가용 OutboundCommand 생성
        EvaluateFinalSubmitCommand evalCommand =
                EvaluateFinalSubmitCommand.from(
                        command.roomId(), sheet, clues, ctx
                );

        // 5) 외부 AI 채점 호출
        AIEvaluateRawResponse raw = evaluateFinalSubmitPort.evaluate(evalCommand);

        // 7) Room 컨텍스트 최종 제출 처리
        ctx.markFinalSubmitted();
        savePort.save(ctx);

        // 8) DB 저장
        FinalSubmitJpaEntity entity =
                new FinalSubmitJpaEntity(
                        command.roomId(),
                        toJson(sheet.getAnswers()),
                        raw.getScore(),
                        raw.getFeedback()
                );

        repository.save(entity);
        deletePort.delete(command.roomId());
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize answers", e);
        }
    }
}