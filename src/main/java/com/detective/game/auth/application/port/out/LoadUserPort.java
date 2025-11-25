package com.detective.game.auth.application.port.out;

import com.detective.game.auth.domain.model.AuthUser;
import java.util.Optional;

public interface LoadUserPort {
    Optional<AuthUser> findBySteamId(String steamId);
    Optional<AuthUser> findById(Long userId);
}
