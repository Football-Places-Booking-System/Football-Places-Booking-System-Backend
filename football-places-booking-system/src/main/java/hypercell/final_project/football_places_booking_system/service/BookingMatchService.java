package hypercell.final_project.football_places_booking_system.service;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTO;
import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;

import hypercell.final_project.football_places_booking_system.repository.BookingMatchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Service layer for booking match logic. Uses in-memory lists for demo/testing.
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingMatchService {

    // In-memory lists for demo purposes. Replace repositories for production.
    private final List<BookingMatch> dummyMatches = new ArrayList<>();
    private List<User> dummyUsers = new ArrayList<>();
    private List<Place> dummyPlaces = new ArrayList<>();
    private List<Team> dummyTeams = new ArrayList<>();
    private final BookingMatchRepository bookingMatchRepository;

    // Creates a booking match after checking for time slot conflicts.
    public BookingMatch createBookingMatch(BookingMatchDTO dto) {
        // Check if the requested time slot is available for the place.
        boolean isAvailable = dummyMatches.stream()
                .filter(m -> m.getPlace().getId().equals(dto.placeId()))
                .noneMatch(existing ->
                        existing.getStartTime().isBefore(dto.endTime()) &&
                                existing.getEndTime().isAfter(dto.startTime())
                );

        if (!isAvailable) {
            throw new IllegalStateException("Time slot is already booked for this place.");
        }

        // Fetch related entities from in-memory lists.
        Place place = dummyPlaces.stream()
                .filter(p -> p.getId().equals(dto.placeId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Place not found"));

        User user = dummyUsers.stream()
                .filter(u -> u.getId().equals(dto.userId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Team team = dummyTeams.stream()
                .filter(t -> t.getId().equals(dto.teamId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        // Build and store the new match.
        BookingMatch match = BookingMatch.builder()
                .id((long) (dummyMatches.size() + 1)) // Simulate auto ID
                .place(place)
                .user(user)
                .team(team)
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .status(MatchStatus.PENDING)
                .build();

        dummyMatches.add(match);
        // bookingMatchRepository.save(match); // Uncomment for real DB
        return match;
    }

    // Cancels a match by setting its status to CANCEL.
    public BookingMatch cancelBooking(Long matchId) {
        BookingMatch match = getById(matchId);
        match.setStatus(MatchStatus.CANCELLED);
        return match;
    }

    // Retrieves a match by its ID.
    public BookingMatch getById(Long id) {
        return dummyMatches.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
    }

    // Returns all matches for a user.
    public List<BookingMatch> getByUser(Long userId) {
        return dummyMatches.stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .toList();
    }

    // Returns all matches for a team.
    public List<BookingMatch> getByTeam(Long teamId) {
        return dummyMatches.stream()
                .filter(m -> m.getTeam().getId().equals(teamId))
                .toList();
    }

    // Returns all matches for a place.
    public List<BookingMatch> getByPlace(Long placeId) {
        return dummyMatches.stream()
                .filter(m -> m.getPlace().getId().equals(placeId))
                .toList();
    }

    // Returns all booking matches in the system.
    public List<BookingMatch> getAll() {
//         return bookingMatchRepository.findAll();

        return new ArrayList<>(dummyMatches);
    }


    // Loads test data into the in-memory lists.
    public void seedData(List<User> users, List<Place> places, List<Team> teams) {
        this.dummyUsers = users;
        this.dummyPlaces = places;
        this.dummyTeams = teams;
    }
}
