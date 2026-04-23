package authservice.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import authservice.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
