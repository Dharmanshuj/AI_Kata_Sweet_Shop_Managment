package com.incubytes.sweetshop.Filter;

import com.incubytes.sweetshop.Services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${auth.whitelist.urls}")
    private String whitelistUrls;

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String uri = request.getRequestURI();
//        return uri.startsWith("/swagger-ui")
//                || uri.startsWith("/v3/api-docs")
//                || uri.startsWith("/api-docs")
//                || uri.startsWith("/swagger-resources")
//                || uri.startsWith("/webjars")
//                || uri.equals("/")                     // optional
//                || uri.equals("/swagger-ui.html");     // optional
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Check if path is whitelisted
        if (isWhitelisted(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
            return;
        }

        String token = authHeader.substring(7);
        
        try {
            if (jwtService.validateToken(token)) {
                String username = jwtService.getUsernameFromToken(token);
                List<GrantedAuthority> authorities = jwtService.getAuthoritiesFromToken(token);
                System.out.println(authorities);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("Invalid JWT token for path: {}", requestPath);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid JWT token\"}");
                return;
            }
        } catch (Exception e) {
            log.error("JWT validation error for path: {}", requestPath, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"JWT validation failed\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isWhitelisted(String path) {
        List<String> whitelist = Arrays.asList(whitelistUrls.split(","));
        return whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern.trim(), path));
    }
}
