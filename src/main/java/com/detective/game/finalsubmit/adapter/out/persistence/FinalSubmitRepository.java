package com.detective.game.finalsubmit.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinalSubmitRepository extends JpaRepository<FinalSubmitJpaEntity, Long> {
    Optional<FinalSubmitJpaEntity> findByRoomId(String roomId);
}

