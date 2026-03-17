package dev.rvg.usermanagement.repository;

import dev.rvg.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing and manipulating {@link User} entities.
 * Provides CRUD operations plus custom query methods.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
