package com.personal.uber_video.security;

import com.personal.uber_video.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    private final JwtUtil jwtUtil;

    public void blacklistToken(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("Attempted to blacklist null or empty token");
            return;
        }
        
        try {
            long expirationTime = jwtUtil.getExpirationTime(token);
            blacklistedTokens.put(token, expirationTime);
            log.info("Total blacklisted tokens: {}", blacklistedTokens.size());
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        Long expirationTime = blacklistedTokens.get(token);
        
        if (expirationTime == null) {
            return false;
        }
        
        // Auto-cleanup expired token
        if (expirationTime < System.currentTimeMillis()) {
            blacklistedTokens.remove(token);
            log.debug("Removed expired token from blacklist");
            return false;
        }
        
        return true;
    }
}
