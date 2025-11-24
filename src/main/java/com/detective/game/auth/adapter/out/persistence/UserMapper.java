package com.detective.game.auth.adapter.out.persistence;

import com.detective.game.auth.domain.model.AuthUser;
import com.detective.game.user.domain.User;

import com.detective.game.user.domain.User.UserBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public AuthUser toDomain(User entity) {
        if (entity == null) {
            return null;
        }

        return new AuthUser(
                entity.getId(),
                entity.getSteamId(),
                entity.getRole(),
                entity.getUsername(),
                entity.getProfileUrl(),
                entity.getAvatarUrl(),
                entity.getAvatarFullUrl(),
                entity.getRealName(),
                entity.getCountryCode(),
                entity.getIsActive(),
                entity.getProfileVisibility(),
                entity.getPersonaState(),
                entity.getTimeCreated(),
                entity.getLastLogin(),
                entity.getLoginCount()
        );
    }

    public User toEntity(AuthUser domain) {
        UserBuilder builder = User.builder()
                .id(domain.getId())
                .steamId(domain.getSteamId())
                .role(domain.getRole())
                .username(domain.getUsername())
                .profileUrl(domain.getProfileUrl())
                .avatarUrl(domain.getAvatarUrl())
                .avatarFullUrl(domain.getAvatarFullUrl())
                .realName(domain.getRealName())
                .countryCode(domain.getCountryCode())
                .isActive(domain.getActive())
                .profileVisibility(domain.getProfileVisibility())
                .personaState(domain.getPersonaState())
                .loginCount(domain.getLoginCount());

        if (domain.getTimeCreated() != null) {
            builder.timeCreated(domain.getTimeCreated());
        }

        if (domain.getLastLogin() != null) {
            builder.lastLogin(domain.getLastLogin());
        }

        return builder.build();
    }
}

