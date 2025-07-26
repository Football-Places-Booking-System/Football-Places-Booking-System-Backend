package hypercell.final_project.football_places_booking_system.repository;

import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository for accessing match participant data in the database.
// Provides methods to find participants by match or user.
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, Long> {
    List<MatchParticipant> findByBookingMatchId(Long bookingMatchId);
    List<MatchParticipant> findByUserId(Long userId);
}
