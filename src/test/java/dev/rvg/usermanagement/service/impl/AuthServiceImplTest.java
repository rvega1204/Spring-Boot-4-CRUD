package dev.rvg.usermanagement.service.impl;

import dev.rvg.usermanagement.dto.LoginRequestDto;
import dev.rvg.usermanagement.dto.LoginResponseDto;
import dev.rvg.usermanagement.entity.User;
import dev.rvg.usermanagement.exception.InvalidCredentialsException;
import dev.rvg.usermanagement.repository.UserRepository;
import dev.rvg.usermanagement.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthServiceImpl authService;

    // ─── login ────────────────────────────────────────────────────

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        User user = new User(1L, "John", "Doe", "John@gmail.com");
        user.setPassword("hashed_password");

        LoginRequestDto request = new LoginRequestDto("John@gmail.com", "password123");

        when(userRepository.findByEmail("John@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "John@gmail.com")).thenReturn("jwt_token");

        LoginResponseDto result = authService.login(request);

        assertThat(result.token()).isEqualTo("jwt_token");
        assertThat(result.userId()).isEqualTo(1L);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenUserNotFound() {
        LoginRequestDto request = new LoginRequestDto("noexiste@gmail.com", "password123");

        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(null);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordIsWrong() {
        User user = new User(1L, "John", "Doe", "John@gmail.com");
        user.setPassword("hashed_password");

        LoginRequestDto request = new LoginRequestDto("John@gmail.com", "wrong_password");

        when(userRepository.findByEmail("John@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void login_shouldNeverGenerateToken_whenCredentialsAreInvalid() {
        LoginRequestDto request = new LoginRequestDto("noexiste@gmail.com", "password123");

        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(null);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtUtil, never()).generateToken(anyLong(), anyString());
    }
}