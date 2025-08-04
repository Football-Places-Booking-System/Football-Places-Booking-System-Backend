package hypercell.final_project.football_places_booking_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;

// Repository for accessing match participant data in the database.
// Provides methods to find participants by match or user.
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, UUID> {
    List<MatchParticipant> findByBookingMatchId(UUID bookingMatchId);

    Optional<Object> findByBookingMatchIdAndUserId(UUID id, UUID id1);
    List<MatchParticipant> findByUserId(UUID userId);
}
