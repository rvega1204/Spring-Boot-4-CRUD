package dev.rvg.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rvg.usermanagement.dto.LoginRequestDto;
import dev.rvg.usermanagement.dto.LoginResponseDto;
import dev.rvg.usermanagement.exception.GlobalExceptionHandler;
import dev.rvg.usermanagement.exception.InvalidCredentialsException;
import dev.rvg.usermanagement.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private AuthService authService;
    @InjectMocks private AuthController authController;

    private static final String BASE_URL = "/api/v1/auth";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── POST /api/v1/auth/login ──────────────────────────────────

    @Test
    void login_shouldReturn200_withToken_whenCredentialsAreValid() throws Exception {
        LoginRequestDto request = new LoginRequestDto("john@gmail.com", "password123");
        LoginResponseDto response = new LoginResponseDto("jwt_token", 1L);

        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt_token"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        LoginRequestDto request = new LoginRequestDto("john@gmail.com", "wrong_password");

        when(authService.login(request)).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    // ─── POST /api/v1/auth/logout ─────────────────────────────────

    @Test
    void logout_shouldReturn204() throws Exception {
        mockMvc.perform(post(BASE_URL + "/logout"))
                .andExpect(status().isNoContent());
    }
}