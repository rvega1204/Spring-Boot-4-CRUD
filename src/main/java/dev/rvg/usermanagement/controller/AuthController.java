package dev.rvg.usermanagement.controller;

import dev.rvg.usermanagement.dto.LoginRequestDto;
import dev.rvg.usermanagement.dto.LoginResponseDto;
import dev.rvg.usermanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Handles login requests and returns JWT tokens.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Creates controller with AuthService dependency.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates user and returns JWT token.
     *
     * @param request login credentials
     * @return JWT token and user ID
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Logs out the user. Client must discard the JWT token.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}
