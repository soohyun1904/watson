package com.detective.game.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthStubConfig.class)
class SteamClientAuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void login_success() throws Exception {

        String body = """
        {
            "auth_ticket": "STUB_TICKET"
        }
        """;

        mockMvc.perform(post("/api/auth/steam-client-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.access_token").value("ACCESS_TOKEN"))
                .andExpect(jsonPath("$.result.refresh_token").value("REFRESH_TOKEN"));
    }
}
