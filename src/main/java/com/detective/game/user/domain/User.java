package com.detective.game.user.domain;

import com.detective.game.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_steam_id", columnList = "steamId"),      // 로그인 시
                @Index(name = "idx_role", columnList = "role"),              // 권한 필터링 시
                @Index(name = "idx_last_login", columnList = "lastLogin")    // 휴면 계정 조회 시
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 고유 ID")
    @EqualsAndHashCode.Include  // equals/hashCode에 id만 포함
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    @Comment("Steam 고유 ID (17자리 숫자)")
    private String steamId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("사용자 권한 (USER, ADMIN, MODERATOR)")
    @Builder.Default  // Builder 사용 시 기본값 적용
    private Role role = Role.USER;  // 기본값: USER

    @Column(nullable = false, length = 100)
    @Comment("Steam 닉네임")
    private String username;

    @Column(length = 500)
    @Comment("Steam 프로필 URL")
    private String profileUrl;

    @Column(length = 500)
    @Comment("Steam 아바타 URL (중간 크기)")
    private String avatarUrl;

    @Column(length = 500)
    @Comment("Steam 아바타 URL (큰 크기)")
    private String avatarFullUrl;

    @Column(length = 100)
    @Comment("Steam 실명 (공개한 경우)")
    private String realName;

    @Column(length = 2)
    @Comment("국가 코드 (ISO 3166-1 alpha-2)")
    private String countryCode;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column
    @Comment("Steam 계정 생성 시간")
    private LocalDateTime timeCreated;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Comment("프로필 공개 여부 (1=Private, 3=Public)")
    private ProfileVisibility profileVisibility;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Comment("온라인 상태 (0=Offline, 1=Online, 2=Busy, etc.)")
    private PersonaState personaState;

    @Column
    @Comment("마지막 로그인 시간")
    private LocalDateTime lastLogin;

    @Column(nullable = false)
    @Comment("로그인 횟수")
    private Integer loginCount;

    public void softDelete() {
        this.isActive = false;
        super.setDeletedAt(LocalDateTime.now());
    }

    public void activate() {
        this.isActive = true;
        super.setDeletedAt(null);
    }
}
