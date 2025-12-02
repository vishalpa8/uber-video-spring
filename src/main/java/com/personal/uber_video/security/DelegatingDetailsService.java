package com.personal.uber_video.security;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Delegating UserDetailsService that tries multiple UserDetailsService implementations in order.
 * This allows authentication to work with both User and Captain tables seamlessly.
 *
 * Marked as @Primary so Spring will use this as the default UserDetailsService bean.
 */
@Service
@Primary
public class DelegatingDetailsService implements UserDetailsService {

    private final List<UserDetailsService> delegates;

    public DelegatingDetailsService(UserDetailService userService,
                                    CaptainDetailsService captainService) {
        this.delegates = List.of(userService, captainService);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsernameNotFoundException lastEx = null;
        for (var delegate : delegates) {
            try {
                return delegate.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex) {
                lastEx = ex;
            }
        }
        throw lastEx != null ? lastEx : new UsernameNotFoundException("User not found: " + username);
    }
}
