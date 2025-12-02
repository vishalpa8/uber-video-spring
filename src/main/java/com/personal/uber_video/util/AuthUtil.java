package com.personal.uber_video.util;

import com.personal.uber_video.entity.Captain;
import com.personal.uber_video.entity.User;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.repository.CaptainRepository;
import com.personal.uber_video.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;
    private final CaptainRepository captainRepository;
    private final JwtUtil jwtUtil;

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ApiException("No user is logged in", HttpStatus.UNAUTHORIZED);
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException("User not logged in", HttpStatus.NOT_FOUND));
    }

    public String extractToken(HttpServletRequest request) {
        String token = jwtUtil.getJwtFromCookie(request);
        if (token == null) {
            token = jwtUtil.getJwtFromHeader(request);
        }
        return token;
    }

    public Captain loggedInCaptain() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ApiException("No captain is logged in", HttpStatus.UNAUTHORIZED);
        }
        return captainRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException("Captain not logged in", HttpStatus.NOT_FOUND));
    }
}
