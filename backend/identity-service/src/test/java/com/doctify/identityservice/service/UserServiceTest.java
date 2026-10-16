package com.doctify.identityservice.service;

import com.doctify.identityservice.dto.AuthResponse;
import com.doctify.identityservice.dto.LoginRequest;
import com.doctify.identityservice.dto.RegisterRequest;
import com.doctify.identityservice.dto.UserResponse;
import com.doctify.identityservice.entity.Role;
import com.doctify.identityservice.entity.User;
import com.doctify.identityservice.repository.RoleRepository;
import com.doctify.identityservice.repository.UserRepository;
import com.doctify.identityservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private Role patientRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        patientRole = new Role("PATIENT");
        ReflectionTestUtils.setField(patientRole, "id", 1L);

        testUser = new User("Alice Smith", "alice@example.com", "hashedPassword123");
        ReflectionTestUtils.setField(testUser, "id", 10L);
        testUser.addRole(patientRole);
    }

    @Test
    @DisplayName("Should successfully register a new patient user")
    void shouldRegisterNewUser() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Alice Smith");
        request.setEmail("alice@example.com");
        request.setPassword("SecurePass123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName("PATIENT")).thenReturn(Optional.of(patientRole));
        when(passwordEncoder.encode("SecurePass123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("Alice Smith", response.getFullName());
        assertTrue(response.getRoles().contains("PATIENT"));

        verify(passwordEncoder).encode("SecurePass123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject registration with duplicate email")
    void shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Alice Smith");
        request.setEmail("alice@example.com");
        request.setPassword("SecurePass123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception if PATIENT role is not initialized")
    void shouldThrowIfRoleNotInitialized() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Alice Smith");
        request.setEmail("alice@example.com");
        request.setPassword("SecurePass123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName("PATIENT")).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> userService.register(request)
        );

        assertTrue(exception.getMessage().contains("PATIENT role has not been initialized"));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials and return JWT AuthResponse")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("alice@example.com", "SecurePass123");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SecurePass123", "hashedPassword123")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("mocked.jwt.token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("alice@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("PATIENT"));
    }

    @Test
    @DisplayName("Should reject login with invalid password")
    void shouldRejectInvalidPassword() {
        LoginRequest request = new LoginRequest("alice@example.com", "WrongPassword");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword123")).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.login(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should reject login with non-existent email")
    void shouldRejectNonExistentEmail() {
        LoginRequest request = new LoginRequest("unknown@example.com", "Password123");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.login(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }
}
