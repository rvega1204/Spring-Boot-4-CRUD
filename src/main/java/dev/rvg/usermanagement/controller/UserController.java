package dev.rvg.usermanagement.controller;

import dev.rvg.usermanagement.dto.CreateUserDto;
import dev.rvg.usermanagement.dto.UserDto;
import dev.rvg.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that exposes user management endpoints.
 * Delegates business logic to {@link UserService}.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user from the provided data.
     *
     * @param createUserDto data used to create the user
     * @return created user with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserDto savedUser = userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * Returns a user by its identifier.
     *
     * @param id unique identifier of the user
     * @return user data with HTTP 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Returns a user by its email address.
     *
     * @param email unique email address of the user
     * @return user data with HTTP 200 status
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Returns all registered users.
     *
     * @return list of all users with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    /**
     * Updates the user that matches the given identifier.
     *
     * @param id      identifier of the user to update
     * @param userDto data used to update the user
     * @return updated user with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,  @RequestBody UserDto userDto) {
        UserDto savedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Deletes the user that matches the given identifier.
     *
     * @param id identifier of the user to delete
     * @return HTTP 204 no content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
