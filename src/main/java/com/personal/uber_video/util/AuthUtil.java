package com.personal.uber_video.util;

import com.personal.uber_video.entity.User;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            throw new ApiException("No user is logged in", HttpStatus.BAD_REQUEST);
        }
        return userRepository.getUserByEmail(authentication.getName());
    }
}
