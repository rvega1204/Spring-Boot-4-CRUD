package dev.rvg.usermanagement.dto;

/**
 * Data transfer object that represents user data exposed by the API.
 * This record is decoupled from the persistence entity to protect the domain model.
 *
 * @param id        unique identifier of the user
 * @param firstName given name of the user
 * @param lastName  family name of the user
 * @param email     unique email address of the user
 */
public record UserDto(Long id, String firstName, String lastName, String email) {
}
