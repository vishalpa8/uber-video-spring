package com.personal.uber_video.security;

import com.personal.uber_video.entity.Captain;
import com.personal.uber_video.repository.CaptainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * UserDetailsService implementation for the Captain table.
 * Loads captain credentials and creates an AppPrincipal with CAPTAIN entity type.
 */
@Service
@RequiredArgsConstructor
public class CaptainDetailsService implements UserDetailsService {

    private final CaptainRepository captainRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Captain captain = captainRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Captain not found: " + email));

        return new AppPrincipal(
                captain.getEmail(),
                captain.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(captain.getRole())),
                captain.getCaptainId(),
                AppPrincipal.EntityType.CAPTAIN
        );
    }
}
