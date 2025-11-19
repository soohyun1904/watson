package com.detective.game.auth.adapter.out.persistence;

import com.detective.game.auth.application.port.out.LoadUserPort;
import com.detective.game.auth.application.port.out.SaveUserPort;
import com.detective.game.auth.domain.model.AuthUser;
import com.detective.game.user.domain.User;
import com.detective.game.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements SaveUserPort, LoadUserPort {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<AuthUser> findBySteamId(String steamId) {
        return userRepository.findBySteamIdAndDeletedAtIsNull(steamId)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<AuthUser> findById(Long userId) {
        return userRepository.findByIdAndIsActiveTrueAndDeletedAtIsNull(userId)
                .map(userMapper::toDomain);
    }

    @Override
    public AuthUser save(AuthUser user) {
        User entity = userMapper.toEntity(user);
        User saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }
}
