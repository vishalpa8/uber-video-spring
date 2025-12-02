package com.personal.uber_video.security;

import com.personal.uber_video.entity.Captain;
import com.personal.uber_video.repository.CaptainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaptainDetailsServiceTest {

    @Mock
    private CaptainRepository captainRepository;

    @InjectMocks
    private CaptainDetailsService captainDetailsService;

    private Captain testCaptain;
    private UUID captainId;

    @BeforeEach
    void setUp() {
        captainId = UUID.randomUUID();
        testCaptain = new Captain();
        testCaptain.setCaptainId(captainId);
        testCaptain.setEmail("captain@example.com");
        testCaptain.setPassword("encodedPassword");
        testCaptain.setRole("ROLE_CAPTAIN");
    }

    @Test
    void loadUserByUsername_Success() {
        when(captainRepository.findByEmail("captain@example.com")).thenReturn(Optional.of(testCaptain));

        UserDetails userDetails = captainDetailsService.loadUserByUsername("captain@example.com");

        assertNotNull(userDetails);
        assertInstanceOf(AppPrincipal.class, userDetails);

        AppPrincipal principal = (AppPrincipal) userDetails;
        assertEquals("captain@example.com", principal.getUsername());
        assertEquals("encodedPassword", principal.getPassword());
        assertEquals(captainId, principal.getEntityId());
        assertEquals(AppPrincipal.EntityType.CAPTAIN, principal.getEntityType());
        assertTrue(principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CAPTAIN")));

        verify(captainRepository, times(1)).findByEmail("captain@example.com");
    }

    @Test
    void loadUserByUsername_WithDifferentRole() {
        testCaptain.setRole("ROLE_DRIVER");
        when(captainRepository.findByEmail("captain@example.com")).thenReturn(Optional.of(testCaptain));

        UserDetails userDetails = captainDetailsService.loadUserByUsername("captain@example.com");
        AppPrincipal principal = (AppPrincipal) userDetails;

        assertTrue(principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_DRIVER")));
    }

    @Test
    void loadUserByUsername_CaptainNotFound_ThrowsException() {
        when(captainRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                captainDetailsService.loadUserByUsername("notfound@example.com"));

        verify(captainRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    void loadUserByUsername_ExceptionMessage() {
        when(captainRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                captainDetailsService.loadUserByUsername("notfound@example.com"));

        assertTrue(exception.getMessage().contains("Captain not found"));
        assertTrue(exception.getMessage().contains("notfound@example.com"));
    }

    @Test
    void loadUserByUsername_ReturnsCorrectEntityType() {
        when(captainRepository.findByEmail("captain@example.com")).thenReturn(Optional.of(testCaptain));

        UserDetails userDetails = captainDetailsService.loadUserByUsername("captain@example.com");
        AppPrincipal principal = (AppPrincipal) userDetails;

        assertEquals(AppPrincipal.EntityType.CAPTAIN, principal.getEntityType());
    }
}
