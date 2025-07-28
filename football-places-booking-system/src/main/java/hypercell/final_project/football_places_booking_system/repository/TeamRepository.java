package hypercell.final_project.football_places_booking_system.repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hypercell.final_project.football_places_booking_system.model.db.Team;

public interface TeamRepository extends JpaRepository<Team , UUID> {
    boolean existsByNameIgnoreCase(String name);
    List<Team> findByCreatorId(UUID creatorId);

    String findTeamNameById(UUID teamId);

    String findTeamDescriptionById(UUID teamId);
}
