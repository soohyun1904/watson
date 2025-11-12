package com.detective.game.steam.service;

import com.detective.game.user.domain.User;
import com.detective.game.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SteamUserService {

    private final UserRepository repository;

    public SteamUserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> findBySteamId(String steamId) {
        return repository.findBySteamIdAndDeletedAtIsNull(steamId);
    }

    public User save(User user) {
        return repository.save(user);
    }

    public boolean existsBySteamId(String steamId) {
        return repository.existsBySteamIdAndDeletedAtIsNull(steamId);
    }
}
