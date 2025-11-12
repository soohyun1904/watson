package com.detective.game.steam.config;

import com.detective.game.steam.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


/**
 * Spring Security 설정
 *
 * 인증 방식:
 * 1. Steam OpenID 로그인 → JWT 발급 (Stateless)
 * 2. JWT 기반 API 인증
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (Stateless JWT 사용)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 사용 안 함 (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 엔드포인트 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // ========================================
                        // Public 엔드포인트 (인증 불필요)
                        // ========================================

                        // Swagger UI
                        .requestMatchers(
                                "/swagger-ui/**",           // Swagger UI 리소스
                                "/swagger-ui.html",         // Swagger UI 메인 페이지
                                "/v3/api-docs/**",          // OpenAPI 3.0 문서
                                "/api-docs/**",             // API 문서
                                "/swagger-resources/**",    // Swagger 리소스
                                "/webjars/**"               // Swagger UI 의존성
                        ).permitAll()

                        // 💡 토큰 디버그 페이지 허용 (추가)
                        .requestMatchers("/token-debug-page").permitAll()

                        // Health Check
                        .requestMatchers("/health", "/actuator/health").permitAll()

                        // Error 페이지
                        .requestMatchers("/error").permitAll()

                        // Steam 인증 (OpenID 로그인)
                        .requestMatchers(
                                "/api/auth/steam/login",
                                "/api/auth/steam/callback"
                        ).permitAll()

                        // 토큰 갱신 (Public)
                        .requestMatchers("/api/auth/refresh").permitAll()

                        // Public API
                        .requestMatchers("/api/public/**").permitAll()

                        // 테스트용 콜백 (개발 환경)
                        .requestMatchers("/test/**").permitAll()


                        // ========================================
                        // Protected 엔드포인트 (인증 필요)
                        // ========================================

                        // Admin 전용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Moderator 전용
                        .requestMatchers("/api/moderator/**").hasAnyRole("ADMIN", "MODERATOR")

                        // 나머지 API는 인증 필요 (USER 이상)
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정
     * 로컬 개발 및 게임 클라이언트 허용
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin (프론트엔드, 게임 클라이언트)
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",           // 로컬 개발
                "https://localhost:*",          // 로컬 HTTPS
                "detective-game://*",           // 게임 클라이언트 Deep Link
                "https://detective-game.com",   // 운영 도메인
                "https://*.detective-game.com"  // 서브도메인
        ));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));

        // 인증 정보 포함 허용 (Cookie, Authorization 헤더)
        configuration.setAllowCredentials(true);

        // 브라우저에서 접근 가능한 응답 헤더
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));

        // Preflight 요청 캐싱 시간 (1시간)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
