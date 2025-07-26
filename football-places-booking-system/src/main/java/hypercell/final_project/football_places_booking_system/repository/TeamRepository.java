package hypercell.final_project.football_places_booking_system.repository;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team , Long> {
    boolean existsByNameIgnoreCase(String name);
    @Query("SELECT t FROM Team t JOIN FETCH t.creator WHERE t.id = :id")
    Optional<Team> findByIdWithCreator(@Param("id") Long id);

}
