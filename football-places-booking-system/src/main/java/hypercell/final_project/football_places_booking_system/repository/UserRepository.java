package hypercell.final_project.football_places_booking_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import hypercell.final_project.football_places_booking_system.model.db.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findByEmail(String username);
    Optional<User> findByEmailIgnoreCase(String email);
}
