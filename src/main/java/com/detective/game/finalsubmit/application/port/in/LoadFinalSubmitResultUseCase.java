package com.detective.game.finalsubmit.application.port.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface LoadFinalSubmitResultUseCase {
    FinalSubmitResultDto load(String roomId);

    @Getter
    @AllArgsConstructor
    class FinalSubmitResultDto {
        private final int score;
        private final String grade;

        public static FinalSubmitResultDto of(int score) {
            return new FinalSubmitResultDto(score, calculateGrade(score));
        }

        private static String calculateGrade(int score) {
            if (score >= 95) return "S";
            if (score >= 85) return "A";
            if (score >= 70) return "B";
            if (score >= 60) return "C";
            return "D";
        }
    }
}
