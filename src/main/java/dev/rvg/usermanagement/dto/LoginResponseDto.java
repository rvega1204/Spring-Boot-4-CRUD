package dev.rvg.usermanagement.dto;

/**
 * Response returned after successful login containing JWT token.
 * Client stores this token for authenticated requests.
 */
public record LoginResponseDto(String token, Long userId) {
}
