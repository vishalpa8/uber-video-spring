package com.personal.uber_video.util;

import com.personal.uber_video.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityValidatorTest {

    @Test
    void testValidInput() {
        assertDoesNotThrow(() -> SecurityValidator.validate("JohnDoe"));
        assertDoesNotThrow(() -> SecurityValidator.validate("test"));
        assertDoesNotThrow(() -> SecurityValidator.validate(null));
        assertDoesNotThrow(() -> SecurityValidator.validate(""));
    }

    @Test
    void testXssScriptTag() {
        ApiException exception = assertThrows(ApiException.class, 
            () -> SecurityValidator.validate("<script>alert('xss')</script>"));
        assertEquals("Invalid input: potentially malicious content detected", exception.getMessage());
    }

    @Test
    void testXssImgTag() {
        ApiException exception = assertThrows(ApiException.class,
            () -> SecurityValidator.validate("<img src=x onerror=alert(1)>"));
        assertEquals("Invalid input: potentially malicious content detected", exception.getMessage());
    }

    @Test
    void testXssJavascript() {
        ApiException exception = assertThrows(ApiException.class,
            () -> SecurityValidator.validate("javascript:alert('xss')"));
        assertEquals("Invalid input: potentially malicious content detected", exception.getMessage());
    }

    @Test
    void testSqlInjection() {
        ApiException exception = assertThrows(ApiException.class,
            () -> SecurityValidator.validate("'; DROP TABLE users--"));
        assertEquals("Invalid input: potentially malicious content detected", exception.getMessage());
    }

    @Test
    void testHtmlIframe() {
        ApiException exception = assertThrows(ApiException.class,
            () -> SecurityValidator.validate("<iframe src='malicious.com'></iframe>"));
        assertEquals("Invalid input: potentially malicious content detected", exception.getMessage());
    }
}
