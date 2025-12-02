package com.personal.uber_video.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppPrincipalTest {

    @Test
    void testCreateAppPrincipalWithUserType() {
        UUID userId = UUID.randomUUID();
        AppPrincipal principal = new AppPrincipal(
                "user@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                userId,
                AppPrincipal.EntityType.USER
        );

        assertEquals("user@example.com", principal.getUsername());
        assertEquals("password", principal.getPassword());
        assertEquals(userId, principal.getEntityId());
        assertEquals(AppPrincipal.EntityType.USER, principal.getEntityType());
        assertTrue(principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testCreateAppPrincipalWithCaptainType() {
        UUID captainId = UUID.randomUUID();
        AppPrincipal principal = new AppPrincipal(
                "captain@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CAPTAIN")),
                captainId,
                AppPrincipal.EntityType.CAPTAIN
        );

        assertEquals("captain@example.com", principal.getUsername());
        assertEquals(captainId, principal.getEntityId());
        assertEquals(AppPrincipal.EntityType.CAPTAIN, principal.getEntityType());
        assertTrue(principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CAPTAIN")));
    }

    @Test
    void testAppPrincipalInheritsFromSpringSecurityUser() {
        UUID userId = UUID.randomUUID();
        AppPrincipal principal = new AppPrincipal(
                "user@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                userId,
                AppPrincipal.EntityType.USER
        );

        assertTrue(principal instanceof org.springframework.security.core.userdetails.User);
        assertTrue(principal.isEnabled());
        assertTrue(principal.isAccountNonExpired());
        assertTrue(principal.isAccountNonLocked());
        assertTrue(principal.isCredentialsNonExpired());
    }
}
