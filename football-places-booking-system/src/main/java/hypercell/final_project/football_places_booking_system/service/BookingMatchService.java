package hypercell.final_project.football_places_booking_system.service;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDTO;
import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
import hypercell.final_project.football_places_booking_system.repository.BookingMatchRepository;
import hypercell.final_project.football_places_booking_system.repository.PlaceRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingMatchService {

    private final BookingMatchRepository bookingMatchRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final TeamRepository teamRepository;

    public BookingMatch createBookingMatch(BookingDTO dto) {
        // Validate Place
        var place = placeRepository.findById(dto.placeId())
                .orElseThrow(() -> new EntityNotFoundException("Place not found"));

        // Validate User
        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Validate Team
        var team = teamRepository.findById(dto.teamId())
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        // Check for time slot availability
        boolean isAvailable = bookingMatchRepository.findByPlaceId(place.getId()).stream()
                .noneMatch(existing ->
                        existing.getStartTime().isBefore(dto.endTime()) &&
                                existing.getEndTime().isAfter(dto.startTime())
                );

        if (!isAvailable) {
            throw new IllegalStateException("The selected time slot is already booked for this place.");
        }

        // Create the booking match
        BookingMatch match = new BookingMatch();
        match.setPlace(place);
        match.setUser(user);
        match.setTeam(team);
        match.setStartTime(dto.startTime());
        match.setEndTime(dto.endTime());
        match.setStatus(MatchStatus.PENDING);

        return bookingMatchRepository.save(match);
    }

    public BookingMatch cancelBooking(UUID matchId) {
        BookingMatch match = getById(matchId);
        match.setStatus(MatchStatus.CANCELLED);
        return bookingMatchRepository.save(match);
    }

    public BookingMatch getById(UUID id) {
        return bookingMatchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
    }

    public List<BookingMatch> getByUser(UUID userId) {
        return bookingMatchRepository.findByUserId(userId);
    }

    public List<BookingMatch> getByTeam(UUID teamId) {
        return bookingMatchRepository.findByTeamId(teamId);
    }

    public List<BookingMatch> getByPlace(UUID placeId) {
        return bookingMatchRepository.findByPlaceId(placeId);
    }

    public List<BookingMatch> getAll() {
        return bookingMatchRepository.findAll();
    }
}
