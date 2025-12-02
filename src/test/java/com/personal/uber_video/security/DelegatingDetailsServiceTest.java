package com.personal.uber_video.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DelegatingDetailsServiceTest {

    @Mock
    private UserDetailService userDetailService;

    @Mock
    private CaptainDetailsService captainDetailsService;

    private DelegatingDetailsService delegatingDetailsService;

    @BeforeEach
    void setUp() {
        delegatingDetailsService = new DelegatingDetailsService(userDetailService, captainDetailsService);
    }

    @Test
    void loadUserByUsername_FoundInUserService() {
        UUID userId = UUID.randomUUID();
        AppPrincipal userPrincipal = new AppPrincipal(
                "user@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                userId,
                AppPrincipal.EntityType.USER
        );

        when(userDetailService.loadUserByUsername("user@example.com")).thenReturn(userPrincipal);

        UserDetails result = delegatingDetailsService.loadUserByUsername("user@example.com");

        assertNotNull(result);
        assertInstanceOf(AppPrincipal.class, result);
        AppPrincipal principal = (AppPrincipal) result;
        assertEquals(AppPrincipal.EntityType.USER, principal.getEntityType());
        assertEquals(userId, principal.getEntityId());

        verify(userDetailService, times(1)).loadUserByUsername("user@example.com");
        verify(captainDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void loadUserByUsername_NotFoundInUserService_FoundInCaptainService() {
        UUID captainId = UUID.randomUUID();
        AppPrincipal captainPrincipal = new AppPrincipal(
                "captain@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CAPTAIN")),
                captainId,
                AppPrincipal.EntityType.CAPTAIN
        );

        when(userDetailService.loadUserByUsername("captain@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));
        when(captainDetailsService.loadUserByUsername("captain@example.com")).thenReturn(captainPrincipal);

        UserDetails result = delegatingDetailsService.loadUserByUsername("captain@example.com");

        assertNotNull(result);
        assertInstanceOf(AppPrincipal.class, result);
        AppPrincipal principal = (AppPrincipal) result;
        assertEquals(AppPrincipal.EntityType.CAPTAIN, principal.getEntityType());
        assertEquals(captainId, principal.getEntityId());

        verify(userDetailService, times(1)).loadUserByUsername("captain@example.com");
        verify(captainDetailsService, times(1)).loadUserByUsername("captain@example.com");
    }

    @Test
    void loadUserByUsername_NotFoundInAnyService_ThrowsException() {
        when(userDetailService.loadUserByUsername("notfound@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));
        when(captainDetailsService.loadUserByUsername("notfound@example.com"))
                .thenThrow(new UsernameNotFoundException("Captain not found"));

        assertThrows(UsernameNotFoundException.class, () ->
                delegatingDetailsService.loadUserByUsername("notfound@example.com"));

        verify(userDetailService, times(1)).loadUserByUsername("notfound@example.com");
        verify(captainDetailsService, times(1)).loadUserByUsername("notfound@example.com");
    }

    @Test
    void loadUserByUsername_PrioritizesUserServiceOverCaptainService() {
        UUID userId = UUID.randomUUID();
        AppPrincipal userPrincipal = new AppPrincipal(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                userId,
                AppPrincipal.EntityType.USER
        );

        // Even if both services have the email, user service is checked first and wins
        when(userDetailService.loadUserByUsername("test@example.com")).thenReturn(userPrincipal);

        UserDetails result = delegatingDetailsService.loadUserByUsername("test@example.com");

        AppPrincipal principal = (AppPrincipal) result;
        assertEquals(AppPrincipal.EntityType.USER, principal.getEntityType());

        verify(userDetailService, times(1)).loadUserByUsername("test@example.com");
        verify(captainDetailsService, never()).loadUserByUsername(anyString());
    }
}
