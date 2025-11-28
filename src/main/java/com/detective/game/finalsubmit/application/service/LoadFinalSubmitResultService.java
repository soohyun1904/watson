package com.detective.game.finalsubmit.application.service;

import com.detective.game.finalsubmit.adapter.out.persistence.FinalSubmitJpaEntity;
import com.detective.game.finalsubmit.adapter.out.persistence.FinalSubmitRepository;
import com.detective.game.finalsubmit.application.port.in.LoadFinalSubmitResultUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadFinalSubmitResultService implements LoadFinalSubmitResultUseCase {

    private final FinalSubmitRepository repository;

    @Override
    public FinalSubmitResultDto load(String roomId) {
        FinalSubmitJpaEntity entity = repository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("제출 결과 없음"));

        return FinalSubmitResultDto.of(entity.getScore(), entity.getGrade(), entity.getFeedback());
    }
}
