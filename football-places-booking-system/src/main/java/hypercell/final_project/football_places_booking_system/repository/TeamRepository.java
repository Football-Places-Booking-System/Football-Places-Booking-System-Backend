package hypercell.final_project.football_places_booking_system.repository;

import hypercell.final_project.football_places_booking_system.model.db.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team , Long> {

}
