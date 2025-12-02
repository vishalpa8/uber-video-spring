package com.personal.uber_video.security;

import com.personal.uber_video.entity.User;
import com.personal.uber_video.repository.UserRepository;
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
class UserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailService userDetailService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole("ROLE_USER");
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertInstanceOf(AppPrincipal.class, userDetails);

        AppPrincipal principal = (AppPrincipal) userDetails;
        assertEquals("test@example.com", principal.getUsername());
        assertEquals("encodedPassword", principal.getPassword());
        assertEquals(userId, principal.getEntityId());
        assertEquals(AppPrincipal.EntityType.USER, principal.getEntityType());
        assertTrue(principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_WithDifferentRole() {
        testUser.setRole("ROLE_ADMIN");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailService.loadUserByUsername("test@example.com");
        AppPrincipal principal = (AppPrincipal) userDetails;

        assertTrue(principal.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailService.loadUserByUsername("notfound@example.com"));

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    void loadUserByUsername_ExceptionMessage() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                userDetailService.loadUserByUsername("notfound@example.com"));

        assertTrue(exception.getMessage().contains("User not found with email"));
        assertTrue(exception.getMessage().contains("notfound@example.com"));
    }

    @Test
    void loadUserByUsername_ReturnsCorrectEntityType() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailService.loadUserByUsername("test@example.com");
        AppPrincipal principal = (AppPrincipal) userDetails;

        assertEquals(AppPrincipal.EntityType.USER, principal.getEntityType());
    }
}
