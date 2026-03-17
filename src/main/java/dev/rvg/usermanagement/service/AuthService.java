package dev.rvg.usermanagement.service;

import dev.rvg.usermanagement.dto.LoginRequestDto;
import dev.rvg.usermanagement.dto.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
}
