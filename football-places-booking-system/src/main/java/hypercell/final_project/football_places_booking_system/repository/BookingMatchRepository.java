package hypercell.final_project.football_places_booking_system.repository;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingMatchRepository extends JpaRepository<BookingMatch, Long> {
    List<BookingMatch> findByTeamId(Long teamId);
    List<BookingMatch> findByUserId(Long userId);
    List<BookingMatch> findByPlaceId(Long placeId);
}

