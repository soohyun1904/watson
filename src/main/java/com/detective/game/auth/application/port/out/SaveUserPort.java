package com.detective.game.auth.application.port.out;

import com.detective.game.auth.domain.model.AuthUser;

public interface SaveUserPort {
    AuthUser save(AuthUser user);
}
