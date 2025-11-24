package com.detective.game.auth.adapter.in.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SteamClientLoginRequest {
    private String authTicket;
}
