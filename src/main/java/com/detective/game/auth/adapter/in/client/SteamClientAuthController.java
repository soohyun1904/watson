package com.detective.game.auth.adapter.in.client;

import com.detective.game.auth.adapter.in.client.dto.SteamClientLoginRequest;
import com.detective.game.auth.application.command.SteamTicketLoginCommand;
import com.detective.game.auth.application.port.in.SteamLoginResult;
import com.detective.game.auth.application.port.in.SteamTicketLoginUseCase;
import com.detective.game.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class SteamClientAuthController {

    private final SteamTicketLoginUseCase steamTicketLoginUseCase;

    @PostMapping("/steam-client-login")
    public ResponseEntity<ApiResponse<SteamLoginResult>> loginFromClient(
            @RequestBody SteamClientLoginRequest request
    ) {

        int ticketLength = Objects.requireNonNullElse(request.getAuthTicket(), "").length();
        log.info("언리얼 클라이언트 로그인 요청: authTicket 길이={}", ticketLength);

        SteamLoginResult result = steamTicketLoginUseCase.loginWithTicket(
                new SteamTicketLoginCommand(request.getAuthTicket())
        );

        return ResponseEntity.ok(
                ApiResponse.success("로그인 성공", result)
        );
    }
}
