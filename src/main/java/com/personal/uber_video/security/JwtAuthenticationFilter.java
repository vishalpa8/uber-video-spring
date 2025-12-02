package com.personal.uber_video.security;

import com.personal.uber_video.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final DelegatingDetailsService delegatingDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    // Public endpoints that should skip JWT authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/user/register",
            "/api/auth/captains/register",
            "/api/auth/user/login",
            "/api/auth/captains/login",
            "/h2-console"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Skip JWT processing for public endpoints
        if (isPublicEndpoint(requestURI)) {
            log.debug("Skipping JWT authentication for public endpoint: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());

        String jwtToken = parseJwt(request);

        try {
            // Check if token is blacklisted
            if(tokenBlacklistService.isBlacklisted(jwtToken)) {
                log.warn("Blacklisted token detected for URI: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Invalid Token!. Please login again.\"}");
                return;
            }

            // Validate and set authentication
            if (jwtToken != null && !jwtToken.isBlank() && jwtUtil.validateJwtToken(jwtToken)){
                String username = jwtUtil.getUsernameFromToken(jwtToken);
                UserDetails userDetails = delegatingDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Roles from Jwt: {}", authentication.getAuthorities());
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request,response);
    }

    private boolean isPublicEndpoint(String requestURI) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(requestURI::startsWith);
    }

    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtil.getJwtFromCookie(request);
        if (jwt != null){
            return jwt;
        }
        return jwtUtil.getJwtFromHeader(request);
    }
}
