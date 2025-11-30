package com.personal.uber_video.util;

import com.personal.uber_video.exception.ApiException;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class SecurityValidator {

    private static final PolicyFactory POLICY = new HtmlPolicyBuilder().toFactory();

    public static void validate(String input) {
        if (input == null || input.isBlank()) {
            return;
        }

        String sanitized = POLICY.sanitize(input);
        
        if (!input.equals(sanitized)) {
            throw new ApiException("Invalid input: potentially malicious content detected", HttpStatus.BAD_REQUEST);
        }
    }
}
