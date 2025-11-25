package com.detective.game.user.repository;

import com.detective.game.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySteamIdAndDeletedAtIsNull(String steamId);
    boolean existsBySteamIdAndDeletedAtIsNull(String steamId);
    Optional<User> findByIdAndIsActiveTrueAndDeletedAtIsNull(Long id);

    // 삭제된 사용자도 포함하여 Steam ID 기반 조회 (관리자/특정 로직용)
    @Query("SELECT u FROM User u WHERE u.steamId = :steamId")
    Optional<User> findBySteamIdWithDeleted(@Param("steamId") String steamId);

    // 삭제된 사용자도 포함하여 ID 기반 조회 (관리자용)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithDeleted(@Param("id") Long id);
}
