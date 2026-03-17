package dev.rvg.usermanagement.service.impl;

import dev.rvg.usermanagement.dto.CreateUserDto;
import dev.rvg.usermanagement.dto.UserDto;
import dev.rvg.usermanagement.entity.User;
import dev.rvg.usermanagement.exception.ResourceNotFoundException;
import dev.rvg.usermanagement.mapper.UserMapper;
import dev.rvg.usermanagement.repository.UserRepository;
import dev.rvg.usermanagement.service.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link UserService}.
 * Contains application business rules for user management.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new {@code UserServiceImpl} with its dependencies injected.
     *
     * @param userRepository repository used to access user data
     * @param userMapper     mapper used to convert between entity and DTO
     */
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        User entity = userMapper.toEntity(createUserDto);
        entity.setPassword(passwordEncoder.encode(createUserDto.password()));
        User savedUser = userRepository.save(entity);
        return userMapper.toDto(savedUser);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no user exists with the given id
     */
    @Override
    public UserDto getUserById(Long id) {
        User user = getUser(id);
        return userMapper.toDto(user);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no user exists with the given email
     */
    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email " + email);
        }

        return userMapper.toDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserDto> getAllUsers() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no user exists with the given id
     */
    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = getUser(id);

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());

        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no user exists with the given id
     */
    @Override
    public void deleteUser(Long id) {
        User user = getUser(id);
        userRepository.delete(user);
    }

    private @NonNull User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }
}
