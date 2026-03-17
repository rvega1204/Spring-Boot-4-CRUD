package dev.rvg.usermanagement.service.impl;

import dev.rvg.usermanagement.dto.LoginRequestDto;
import dev.rvg.usermanagement.dto.LoginResponseDto;
import dev.rvg.usermanagement.entity.User;
import dev.rvg.usermanagement.exception.InvalidCredentialsException;
import dev.rvg.usermanagement.repository.UserRepository;
import dev.rvg.usermanagement.security.JwtUtil;
import dev.rvg.usermanagement.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Constructor with dependency injection for repository, encoder and JWT util.
     */
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates user by email and password.
     * Returns JWT token if credentials valid.
     *
     * @param request login credentials (email + password)
     * @return JWT token and user ID
     * @throws RuntimeException if credentials invalid
     */
    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email());

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new LoginResponseDto(token, user.getId());
    }
}

