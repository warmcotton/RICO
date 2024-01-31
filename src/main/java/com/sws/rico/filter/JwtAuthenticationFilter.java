package com.sws.rico.filter;

import com.sws.rico.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request.getHeader("Authorization"));
                if (token != null && jwtTokenProvider.validateToken(token)) {
            if(ObjectUtils.isEmpty(redisTemplate.opsForValue().get("LOGOUT:"+token))) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if(authentication != null) SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            token = token.substring(7);
        } else token = null;
        return token;
    }
}
