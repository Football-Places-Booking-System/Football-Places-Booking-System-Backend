package hypercell.final_project.football_places_booking_system.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hypercell.final_project.football_places_booking_system.model.db.User;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findByEmail(String username);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findById(UUID creatorid);

    @Query("SELECT u.username FROM User u WHERE u.id = :id")
    String findUsernameById(@Param("id") UUID id);
}
