package com.detective.game.auth.application.service;

import com.detective.game.auth.application.command.SteamTicketLoginCommand;
import com.detective.game.auth.application.port.out.*;
import com.detective.game.auth.application.port.out.dto.SteamProfile;
import com.detective.game.auth.domain.model.AuthUser;
import com.detective.game.auth.application.port.out.dto.TokenPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SteamTicketLoginServiceTest {

    @Mock
    SteamTicketVerifyPort verifyPort;
    @Mock
    LoadSteamProfilePort steamProfilePort;
    @Mock
    LoadUserPort loadUserPort;
    @Mock
    SaveUserPort saveUserPort;
    @Mock
    JwtPort jwtPort;
    @Mock RefreshTokenPort refreshTokenPort;

    @InjectMocks
    SteamTicketLoginService service;

    private SteamProfile stubProfile;

    @BeforeEach
    void setup() {
        stubProfile = new SteamProfile(
                "76561198000000000",
                3,
                1,
                "TestUser",
                "url",
                "ava",
                "avafull",
                "real",
                "KR",
                10000L
        );
    }

    @Test
    void login_new_user_success() {
        // given
        String ticket = "TEST_TICKET";

        when(verifyPort.verifyTicketAndGetSteamId(ticket))
                .thenReturn("76561198000000000");

        when(steamProfilePort.loadSteamProfile("76561198000000000"))
                .thenReturn(stubProfile);

        when(loadUserPort.findBySteamId("76561198000000000"))
                .thenReturn(Optional.empty());

        when(saveUserPort.save(any()))
                .thenAnswer(invocation -> {
                    AuthUser u = invocation.getArgument(0);
                    return new AuthUser(
                            1L,
                            u.getSteamId(),
                            u.getRole(),
                            u.getUsername(),
                            u.getProfileUrl(),
                            u.getAvatarUrl(),
                            u.getAvatarFullUrl(),
                            u.getRealName(),
                            u.getCountryCode(),
                            true,
                            u.getProfileVisibility(),
                            u.getPersonaState(),
                            u.getTimeCreated(),
                            LocalDateTime.now(),
                            1
                    );
                });

        when(jwtPort.generateTokens(any(), any(), any()))
                .thenReturn(new TokenPair("ACCESS", "REFRESH"));

        when(jwtPort.getExpirationFromToken("REFRESH"))
                .thenReturn(java.util.Date.from(Instant.now().plusSeconds(1000)));

        // when
        var result = service.loginWithTicket(new SteamTicketLoginCommand(ticket));

        // then
        assertThat(result.getAccessToken()).isEqualTo("ACCESS");
        assertThat(result.getRefreshToken()).isEqualTo("REFRESH");

        verify(verifyPort, times(1))
                .verifyTicketAndGetSteamId(ticket);

        verify(steamProfilePort, times(1))
                .loadSteamProfile("76561198000000000");

        verify(saveUserPort, times(1)).save(any());
        verify(refreshTokenPort, times(1))
                .save(eq("REFRESH"), eq(1L), any());
    }
}