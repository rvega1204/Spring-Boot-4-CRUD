package dev.rvg.usermanagement.exception;

import dev.rvg.usermanagement.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks private GlobalExceptionHandler handler;

    // ─── ResourceNotFoundException ────────────────────────────────

    @Test
    void handleResourceNotFound_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found with id 99");

        ResponseEntity<ErrorResponseDto> response = handler.handleResourceNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().message()).isEqualTo("User not found with id 99");
        assertThat(response.getBody().status()).isEqualTo(404);
    }

    // ─── InvalidCredentialsException ─────────────────────────────

    @Test
    void handleInvalidCredentials_shouldReturn401() {
        InvalidCredentialsException ex = new InvalidCredentialsException();

        ResponseEntity<ErrorResponseDto> response = handler.handleInvalidCredentials(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("Invalid email or password");
    }

    // ─── DataIntegrityViolationException ─────────────────────────

    @Test
    void handleDuplicateEmail_shouldReturn409() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate entry");

        ResponseEntity<ErrorResponseDto> response = handler.handleDuplicateEmail(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().message()).isEqualTo("Email is already registered");
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    // ─── MethodArgumentNotValidException ─────────────────────────

    @Test
    void handleValidationErrors_shouldReturn400_withFieldMessages() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("createUserDto", "email", "Email format is invalid");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponseDto> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).contains("email");
        assertThat(response.getBody().message()).contains("Email format is invalid");
    }

    // ─── Generic Exception ────────────────────────────────────────

    @Test
    void handleGeneric_shouldReturn500() {
        Exception ex = new Exception("Unexpected failure");

        ResponseEntity<ErrorResponseDto> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message()).isEqualTo("Unexpected error occurred");
        assertThat(response.getBody().status()).isEqualTo(500);
    }
}