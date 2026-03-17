package dev.rvg.usermanagement.mapper;

import dev.rvg.usermanagement.dto.CreateUserDto;
import dev.rvg.usermanagement.dto.UserDto;
import dev.rvg.usermanagement.entity.User;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link dev.rvg.usermanagement.entity.User} and
 * {@link dev.rvg.usermanagement.dto.UserDto}.
 * This component isolates mapping logic from business services.
 */
@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public User toEntity(CreateUserDto dto) {
        User user = new User();
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        return user;
    }

}
