package com.detective.game.auth.adapter.out.persistence;

import com.detective.game.auth.application.port.out.RefreshTokenPort;
import com.detective.game.auth.domain.model.AuthRefreshToken;
import com.detective.game.auth.domain.model.AuthUser;
import com.detective.game.common.exception.ErrorMessage;
import com.detective.game.common.exception.RefreshTokenException;
import com.detective.game.common.exception.UserException;
import com.detective.game.auth.infrastructure.jwt.JwtTokenProvider;
import com.detective.game.auth.adapter.out.persistence.entity.RefreshToken;
import com.detective.game.user.domain.User;
import com.detective.game.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import static com.detective.game.common.exception.ErrorMessage.REFRESH_TOKEN_DELETE_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class RefreshTokenPersistenceAdapter implements RefreshTokenPort {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public boolean validate(String tokenValue) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByRefreshToken(tokenValue);

        if (refreshTokenOpt.isEmpty()) {
            log.error("존재하지 않는 Refresh Token: {}", tokenValue);
            return false;
        }

        jwtTokenProvider.validateToken(tokenValue);

        if (!jwtTokenProvider.isRefreshToken(tokenValue)) {
            log.error("Refresh Token이 아닌 토큰: {}", tokenValue);
            return false;
        }

        AuthRefreshToken token = refreshTokenMapper.toDomain(refreshTokenOpt.get());
        if (token.isExpired() || token.isRevoked()) {
            log.error("만료되었거나 해지된 Refresh Token: {}", tokenValue);
            return false;
        }

        return true;
    }

    @Override
    public Optional<AuthUser> getUserByRefreshToken(String tokenValue) {
        return refreshTokenRepository.findByRefreshToken(tokenValue)
                .flatMap(token -> userRepository.findById(token.getUserId()))
                .filter(User::getIsActive)
                .map(userMapper::toDomain);
    }

    @Override
    public void save(String refreshToken, Long userId, Instant expiryDate) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserException(ErrorMessage.USER_NOT_FOUND);
            }

            refreshTokenRepository.findByUserId(userId)
                    .ifPresentOrElse(
                            token -> {
                                token.renew(refreshToken, expiryDate);
                                log.info("event=refresh_token_updated, user_id={}", userId);
                            },
                            () -> {
                                RefreshToken newToken = RefreshToken.builder()
                                        .refreshToken(refreshToken)
                                        .userId(userId)
                                        .expiryDate(expiryDate)
                                        .revoked(false)
                                        .build();
                                refreshTokenRepository.save(newToken);
                                log.info("event=refresh_token_created, user_id={}", userId);
                            }
                    );
        } catch (Exception e) {
            log.error("event=refresh_token_save_failed, user_id={}, error_message={}", userId, e.getMessage(), e);
            throw new RefreshTokenException(ErrorMessage.REFRESH_TOKEN_SAVE_ERROR);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        try {
            refreshTokenRepository.deleteByUserId(userId);
            log.info("event=refresh_token_deleted, user_id={}", userId);
        } catch (Exception e) {
            log.error("event=refresh_token_delete_failed, user_id={}, error_message={}", userId, e.getMessage(), e);
            throw new RefreshTokenException(REFRESH_TOKEN_DELETE_ERROR);
        }
    }
}
