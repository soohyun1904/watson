package com.detective.game.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        // Security Scheme 이름
        String jwtSchemeName = "Bearer Authentication";

        // Security Requirement (전역 적용)
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        // Security Scheme 정의
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .description("JWT 액세스 토큰을 입력하세요 (Bearer 접두사 제외)")
                );

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                // 서버 정보 추가
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("로컬 개발 서버"),
                        new Server()
                                .url("https://api.detective-game.com")
                                .description("운영 서버")
                ))
                // 전역 Security Requirement (선택사항)
                // .addSecurityItem(securityRequirement)
                // ↑ 주석: 모든 API에 🔒 표시. 개별 API에만 적용하려면 주석 처리
                ;
    }

    private Info apiInfo() {
        return new Info()
                .title("Detective Game API")
                .description("""
                ## 도와줘! 왓슨! - 추리 게임 백엔드 API
                
                Steam 인증 기반 1923년 황옥 경부 폭탄사건 추리 게임
                
                ### 🔐 인증 방법
                1. **Steam 로그인**: `GET /api/auth/steam/login`으로 브라우저 열기
                2. Steam 로그인 완료 후 `accessToken` 획득
                3. 우측 상단 **🔓 Authorize** 버튼 클릭
                4. `accessToken` 입력 (Bearer 접두사 제외)
                5. 🔒 표시된 API 테스트 가능
                
                ### 📌 주요 기능
                - **Steam OpenID 인증**: 별도 회원가입 없이 Steam 계정으로 로그인
                - **JWT 기반 API 인증**: Stateless 인증 방식
                - **AI 챗봇 대화**: RAG 기반 증거 해석
                - **멀티플레이**: 최대 4명 협동 플레이
                
                ### 🎮 게임 흐름
                1. Steam 로그인 → JWT 발급
                2. 게임 시작 (싱글/멀티)
                3. 증거 수집 → AI 조수와 대화
                4. 최종 보고서 작성 → AI 채점
                
                ### 📚 관련 문서
                - [GitHub](https://github.com/your-repo)
                - [프로젝트 문서](https://docs.detective-game.com)
                """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Detective Game Team")
                        .email("support@detective-game.com")
                        .url("https://detective-game.com")
                )
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                );
    }
}