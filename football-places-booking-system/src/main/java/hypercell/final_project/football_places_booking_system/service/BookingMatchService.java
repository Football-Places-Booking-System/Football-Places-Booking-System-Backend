package hypercell.final_project.football_places_booking_system.service;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTO;
import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingMatchService {

    // FAKE DATA (replace with @Autowired repositories later)
    private final List<BookingMatch> dummyMatches = new ArrayList<>();
    private List<User> dummyUsers = new ArrayList<>();
    private List<Place> dummyPlaces = new ArrayList<>();
    private List<Team> dummyTeams = new ArrayList<>();

    public BookingMatch createBookingMatch(BookingMatchDTO dto) {
        // Step 1: Check for time slot overlap
        boolean isAvailable = dummyMatches.stream()
                .filter(m -> m.getPlace().getId().equals(dto.placeId()))
                .noneMatch(existing ->
                        existing.getStartTime().isBefore(dto.endTime()) &&
                                existing.getEndTime().isAfter(dto.startTime())
                );

        if (!isAvailable) {
            throw new IllegalStateException("Time slot is already booked for this place.");
        }

        // Step 2: Fake fetches entities
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

        // Step 3: Create and store match
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
        return match;
    }

    public BookingMatch cancelBooking(Long matchId) {
        BookingMatch match = getById(matchId);
        match.setStatus(MatchStatus.CANCELLED);
        return match;
    }

    public BookingMatch getById(Long id) {
        return dummyMatches.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
    }

    public List<BookingMatch> getByUser(Long userId) {
        return dummyMatches.stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .toList();
    }

    public List<BookingMatch> getByTeam(Long teamId) {
        return dummyMatches.stream()
                .filter(m -> m.getTeam().getId().equals(teamId))
                .toList();
    }

    // Optional: Method to preload fake data for testing
    public void seedData(List<User> users, List<Place> places, List<Team> teams) {
        this.dummyUsers = users;
        this.dummyPlaces = places;
        this.dummyTeams = teams;
    }
}
