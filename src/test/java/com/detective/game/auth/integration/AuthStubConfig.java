package com.detective.game.auth.integration;

import com.detective.game.auth.application.port.out.JwtPort;
import com.detective.game.auth.application.port.out.LoadSteamProfilePort;
import com.detective.game.auth.application.port.out.SteamTicketVerifyPort;
import com.detective.game.auth.stub.JwtPortStub;
import com.detective.game.auth.stub.LoadSteamProfilePortStub;
import com.detective.game.auth.stub.SteamTicketVerifyPortStub;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AuthStubConfig {

    @Bean
    public SteamTicketVerifyPort steamTicketVerifyPort() {
        return new SteamTicketVerifyPortStub();
    }

    @Bean
    public LoadSteamProfilePort loadSteamProfilePort() {
        return new LoadSteamProfilePortStub();
    }

    @Bean
    public JwtPort jwtPort() {
        return new JwtPortStub();
    }
}
