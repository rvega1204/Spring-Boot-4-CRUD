package dev.rvg.usermanagement.service.impl;

import dev.rvg.usermanagement.dto.CreateUserDto;
import dev.rvg.usermanagement.dto.UserDto;
import dev.rvg.usermanagement.entity.User;
import dev.rvg.usermanagement.exception.ResourceNotFoundException;
import dev.rvg.usermanagement.mapper.UserMapper;
import dev.rvg.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserServiceImpl userService;

    // ─── createUser ───────────────────────────────────────────────

    @Test
    void createUser_shouldReturnSavedUserDto() {
        CreateUserDto createUserDto = new CreateUserDto("John", "Doe", "John@gmail.com", "password123");
        User entity = new User(null, "John", "Doe", "John@gmail.com");
        User savedUser = new User(1L, "John", "Doe", "John@gmail.com");
        UserDto expectedDto = new UserDto(1L, "John", "Doe", "John@gmail.com");

        when(userMapper.toEntity(createUserDto)).thenReturn(entity);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(entity)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

        UserDto result = userService.createUser(createUserDto);

        assertThat(result).isEqualTo(expectedDto);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(entity);
    }

    // ─── getUserById ──────────────────────────────────────────────

    @Test
    void getUserById_shouldReturnUserDto_whenUserExists() {
        User user = new User(1L, "John", "Doe", "John@gmail.com");
        UserDto expectedDto = new UserDto(1L, "John", "Doe", "John@gmail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getUserById(1L);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void getUserById_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ─── getUserByEmail ───────────────────────────────────────────

    @Test
    void getUserByEmail_shouldReturnUserDto_whenEmailExists() {
        User user = new User(1L, "John", "Doe", "John@gmail.com");
        UserDto expectedDto = new UserDto(1L, "John", "Doe", "John@gmail.com");

        when(userRepository.findByEmail("John@gmail.com")).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getUserByEmail("John@gmail.com");

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void getUserByEmail_shouldThrowResourceNotFoundException_whenEmailNotFound() {
        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(null);

        assertThatThrownBy(() -> userService.getUserByEmail("noexiste@gmail.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getAllUsers ──────────────────────────────────────────────

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<User> users = List.of(
                new User(1L, "John", "Doe", "John@gmail.com"),
                new User(2L, "Juan", "Perez", "juan@gmail.com")
        );
        List<UserDto> expectedDtos = List.of(
                new UserDto(1L, "John", "Doe", "John@gmail.com"),
                new UserDto(2L, "Juan", "Perez", "juan@gmail.com")
        );

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(users.get(0))).thenReturn(expectedDtos.get(0));
        when(userMapper.toDto(users.get(1))).thenReturn(expectedDtos.get(1));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(2).isEqualTo(expectedDtos);
    }

    // ─── updateUser ───────────────────────────────────────────────

    @Test
    void updateUser_shouldReturnUpdatedUserDto() {
        User existingUser = new User(1L, "John", "Doe", "John@gmail.com");
        UserDto updateDto = new UserDto(1L, "John", "Lopez", "John@gmail.com");
        UserDto expectedDto = new UserDto(1L, "John", "Lopez", "John@gmail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDto(existingUser)).thenReturn(expectedDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.lastName()).isEqualTo("Lopez");
    }

    @Test
    void updateUser_shouldThrowResourceNotFoundException_whenUserNotFound() {
        UserDto updateDto = new UserDto(99L, "John", "Lopez", "John@gmail.com");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, updateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── deleteUser ───────────────────────────────────────────────

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        User user = new User(1L, "John", "Doe", "John@gmail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowResourceNotFoundException_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}