package com.detective.game.steam.respository;

import com.detective.game.steam.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    // 만료된 토큰 삭제
    void deleteByExpiryDateBefore(Instant now);

    // 해지된 토큰 삭제
    void deleteByRevokedTrue();
}
