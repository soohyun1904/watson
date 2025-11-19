package com.detective.game.auth.application.service;

import com.detective.game.auth.application.command.LogoutCommand;
import com.detective.game.auth.application.port.in.LogoutUseCase;
import com.detective.game.auth.application.port.out.RefreshTokenPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LogoutServiceImpl implements LogoutUseCase {
    private final RefreshTokenPort refreshTokenPort;

    @Override
    public void logout(LogoutCommand command) {
        Long userId = command.userId();
        refreshTokenPort.deleteByUserId(userId);
        log.info("event=user_logged_out, user_id={}", userId);
    }
}
