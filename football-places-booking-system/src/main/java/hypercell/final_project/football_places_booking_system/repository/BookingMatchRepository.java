package hypercell.final_project.football_places_booking_system.repository;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingMatchRepository extends JpaRepository<BookingMatch, Long> {
    List<BookingMatch> findByTeamId(Long teamId);
    List<BookingMatch> findByUserId(UUID userId);
    List<BookingMatch> findByPlaceId(Long placeId);
}

