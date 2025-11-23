package com.detective.game.auth.adapter.out.persistence;

import com.detective.game.auth.domain.model.AuthRefreshToken;
import com.detective.game.auth.adapter.out.persistence.entity.RefreshToken;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public AuthRefreshToken toDomain(RefreshToken entity) {
        if (entity == null) return null;

        return new AuthRefreshToken(
                entity.getUserId(),
                entity.getRefreshToken(),
                entity.getExpiryDate(),
                entity.isRevoked()
        );
    }
}