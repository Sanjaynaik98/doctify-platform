package com.doctify.identityservice.security;

import com.doctify.identityservice.entity.Role;
import com.doctify.identityservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "DoctifyPlatformSecureSecretKeyForJwtTokenGeneration2026!Healthcare");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L); // 1 hour
        jwtService.init();
    }

    @Test
    @DisplayName("Should generate a valid JWT token for a user")
    void shouldGenerateValidToken() {
        User user = new User("Jane Doe", "jane.doe@example.com", "encodedPassword");
        ReflectionTestUtils.setField(user, "id", 1L);
        user.addRole(new Role("PATIENT"));

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtService.validateToken(token));
        assertEquals("jane.doe@example.com", jwtService.extractUsername(token));
        assertEquals(1L, jwtService.extractUserId(token));
        List<String> roles = jwtService.extractRoles(token);
        assertTrue(roles.contains("PATIENT"));
    }

    @Test
    @DisplayName("Should validate token against UserPrincipal")
    void shouldValidateTokenAgainstUserPrincipal() {
        User user = new User("Jane Doe", "jane.doe@example.com", "encodedPassword");
        ReflectionTestUtils.setField(user, "id", 1L);
        user.addRole(new Role("PATIENT"));

        String token = jwtService.generateToken(user);
        UserPrincipal principal = UserPrincipal.create(user);

        assertTrue(jwtService.isTokenValid(token, principal));
    }

    @Test
    @DisplayName("Should reject invalid or corrupted token")
    void shouldRejectInvalidToken() {
        assertFalse(jwtService.validateToken("invalid.token.structure"));
    }
}
