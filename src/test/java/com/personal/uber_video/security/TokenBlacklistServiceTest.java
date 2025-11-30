package com.personal.uber_video.security;

import com.personal.uber_video.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String EXPIRED_TOKEN = "expired.jwt.token";

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService(jwtUtil);
    }

    @Test
    void blacklistToken_Success() {
        long futureTime = System.currentTimeMillis() + 86400000;
        when(jwtUtil.getExpirationTime(VALID_TOKEN)).thenReturn(futureTime);

        tokenBlacklistService.blacklistToken(VALID_TOKEN);

        assertTrue(tokenBlacklistService.isBlacklisted(VALID_TOKEN));
        verify(jwtUtil).getExpirationTime(VALID_TOKEN);
    }

    @Test
    void blacklistToken_ExceptionHandled() {
        when(jwtUtil.getExpirationTime(VALID_TOKEN)).thenThrow(new RuntimeException("Invalid token"));

        tokenBlacklistService.blacklistToken(VALID_TOKEN);

        assertFalse(tokenBlacklistService.isBlacklisted(VALID_TOKEN));
    }

    @Test
    void isBlacklisted_TokenNotInList() {
        assertFalse(tokenBlacklistService.isBlacklisted(VALID_TOKEN));
    }

    @Test
    void isBlacklisted_TokenInListAndValid() {
        long futureTime = System.currentTimeMillis() + 86400000;
        when(jwtUtil.getExpirationTime(VALID_TOKEN)).thenReturn(futureTime);

        tokenBlacklistService.blacklistToken(VALID_TOKEN);

        assertTrue(tokenBlacklistService.isBlacklisted(VALID_TOKEN));
    }

    @Test
    void isBlacklisted_TokenExpired_RemovedFromList() {
        long pastTime = System.currentTimeMillis() - 1000;
        when(jwtUtil.getExpirationTime(EXPIRED_TOKEN)).thenReturn(pastTime);

        tokenBlacklistService.blacklistToken(EXPIRED_TOKEN);

        assertFalse(tokenBlacklistService.isBlacklisted(EXPIRED_TOKEN));
        assertFalse(tokenBlacklistService.isBlacklisted(EXPIRED_TOKEN));
    }

    @Test
    void isBlacklisted_MultipleTokens() {
        String token1 = "token1";
        String token2 = "token2";
        long futureTime = System.currentTimeMillis() + 86400000;

        when(jwtUtil.getExpirationTime(token1)).thenReturn(futureTime);
        when(jwtUtil.getExpirationTime(token2)).thenReturn(futureTime);

        tokenBlacklistService.blacklistToken(token1);
        tokenBlacklistService.blacklistToken(token2);

        assertTrue(tokenBlacklistService.isBlacklisted(token1));
        assertTrue(tokenBlacklistService.isBlacklisted(token2));
    }

    @Test
    void isBlacklisted_ExpiredTokenAutoRemoved() {
        long pastTime = System.currentTimeMillis() - 5000;
        when(jwtUtil.getExpirationTime(EXPIRED_TOKEN)).thenReturn(pastTime);

        tokenBlacklistService.blacklistToken(EXPIRED_TOKEN);
        
        boolean firstCheck = tokenBlacklistService.isBlacklisted(EXPIRED_TOKEN);
        boolean secondCheck = tokenBlacklistService.isBlacklisted(EXPIRED_TOKEN);

        assertFalse(firstCheck);
        assertFalse(secondCheck);
    }

    @Test
    void blacklistToken_NullToken() {
        when(jwtUtil.getExpirationTime(null)).thenThrow(new IllegalArgumentException("Token cannot be null"));

        tokenBlacklistService.blacklistToken(null);

        assertFalse(tokenBlacklistService.isBlacklisted(null));
    }

    @Test
    void isBlacklisted_ConcurrentAccess() throws InterruptedException {
        long futureTime = System.currentTimeMillis() + 86400000;
        when(jwtUtil.getExpirationTime(VALID_TOKEN)).thenReturn(futureTime);

        tokenBlacklistService.blacklistToken(VALID_TOKEN);

        Thread t1 = new Thread(() -> assertTrue(tokenBlacklistService.isBlacklisted(VALID_TOKEN)));
        Thread t2 = new Thread(() -> assertTrue(tokenBlacklistService.isBlacklisted(VALID_TOKEN)));

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
