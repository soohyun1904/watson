package com.detective.game.finalsubmit.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "final_submit")
@Getter
@NoArgsConstructor
public class FinalSubmitJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;

    @Column(columnDefinition = "TEXT")
    private String answersJson;   // answers 배열을 JSON 문자열로 저장

    private Integer score;        // 나중에 평가 점수

//    피드백 추가 예정
//    @Column(columnDefinition = "TEXT")
//    private String feedback;

    private LocalDateTime submittedAt;

    public FinalSubmitJpaEntity(String roomId, String answersJson, Integer score) {
        this.roomId = roomId;
        this.answersJson = answersJson;
        this.score = score;
        this.submittedAt = LocalDateTime.now();
    }
}
