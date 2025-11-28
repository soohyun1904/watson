package com.detective.game.finalsubmit.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface LoadFinalSubmitResultUseCase {
    FinalSubmitResultDto load(String roomId);

    @Getter
    @AllArgsConstructor
    class FinalSubmitResultDto {
        private final Integer score;
        private final String grade;
        private final String feedback;

        public static FinalSubmitResultDto of(Integer score, String grade, String feedback) {
            return new FinalSubmitResultDto(score, grade, feedback);
        }
    }
}
