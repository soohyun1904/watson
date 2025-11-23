package com.detective.game.auth.adapter.out.persistence.entity;

import com.detective.game.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked;

    /**
     * 리프레시 토큰 갱신
     * @param newToken 새 리프레시 토큰 문자열
     */
    public void renew(String newToken, Instant expiryDate) {
        this.refreshToken = newToken;
        this.expiryDate = expiryDate;
    }
}
