package hypercell.final_project.football_places_booking_system.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;

@Repository
public interface BookingMatchRepository extends JpaRepository<BookingMatch, UUID> {
    List<BookingMatch> findByTeamId(UUID teamId);
    List<BookingMatch> findByUserId(UUID userId);
    List<BookingMatch> findByPlaceId(UUID placeId);
}

