package com.detective.game.auth.domain.model;

import com.detective.game.user.domain.PersonaState;
import com.detective.game.user.domain.ProfileVisibility;
import com.detective.game.user.domain.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AuthUser {
    private final Long id;
    private final String steamId;
    private final Role role;
    private final String username;
    private final String profileUrl;
    private final String avatarUrl;
    private final String avatarFullUrl;
    private final String realName;
    private final String countryCode;
    private final Boolean active;
    private final ProfileVisibility profileVisibility;
    private final PersonaState personaState;
    private final LocalDateTime timeCreated;
    private final LocalDateTime lastLogin;
    private final Integer loginCount;
}
