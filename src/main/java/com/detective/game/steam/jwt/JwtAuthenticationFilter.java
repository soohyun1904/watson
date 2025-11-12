package com.detective.game.steam.jwt;

import com.detective.game.common.exception.ErrorMessage;
import com.detective.game.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static com.detective.game.common.exception.ErrorMessage.AUTH_INVALID_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 이미 인증된 경우 스킵
            if(SecurityContextHolder.getContext().getAuthentication() != null){
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                //토큰 유효성 검증
                tokenProvider.validateToken(jwt);

                // Access Token인지 확인
                if (tokenProvider.isAccessToken(jwt)) {
                    String steamId = tokenProvider.getSteamIdFromToken(jwt);
                    Long userId = tokenProvider.getUserIdFromToken(jwt);
                    String role = tokenProvider.getRoleFromToken(jwt).orElse("ROLE_CUSTOMER");

                    UserDetailsImpl userDetails = new UserDetailsImpl(
                            userId,
                            steamId,
                            null,  // username - 필요시에만 별도 API로 조회
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                    );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("사용자 인증 설정 완료: userId={}, steamId={}, role={}", userId, steamId, role);
                }
            }
        } catch (Exception ex) {
            log.error("event=jwt_auth_failed, error_message={}", ex.getMessage(), ex);
            sendErrorResponse(response, AUTH_INVALID_TOKEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorMessage errorMessage) throws IOException {
        response.setStatus(errorMessage.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.error(errorMessage.getHttpStatus().value(), errorMessage.getMessage());
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);

        response.getWriter().write(jsonResponse);
    }
}
