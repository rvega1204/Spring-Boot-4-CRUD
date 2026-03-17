package dev.rvg.usermanagement.service;

import dev.rvg.usermanagement.dto.CreateUserDto;
import dev.rvg.usermanagement.dto.UserDto;

import java.util.List;

/**
 * Defines the contract for managing users in the application.
 * Exposes high-level operations that the application layer can consume.
 */
public interface UserService {

    UserDto createUser(CreateUserDto createUserDto);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
}
