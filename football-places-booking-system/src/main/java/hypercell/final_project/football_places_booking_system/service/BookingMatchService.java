package hypercell.final_project.football_places_booking_system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.Place;
import hypercell.final_project.football_places_booking_system.model.db.Team;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTO;
import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingMatchService {

    // Fake data lists (used until DB is fully integrated)
    private final List<BookingMatch> dummyMatches = new ArrayList<>();
    private List<User> dummyUsers = new ArrayList<>();
    private List<Place> dummyPlaces = new ArrayList<>();
    private List<Team> dummyTeams = new ArrayList<>();

    // Real repositories (used later when DB is ready)
//    private final BookingMatchRepository bookingMatchRepository;
//    private final UserRepository userRepository;
//    private final PlaceRepository placeRepository;
//    private final TeamRepository teamRepository;

    public BookingMatch createBookingMatch(BookingMatchDTO dto) {
        // Fake logic for overlap check
        boolean isAvailable = dummyMatches.stream()
                .filter(m -> m.getPlace().getId().equals(dto.placeId()))
                .noneMatch(existing ->
                        existing.getStartTime().isBefore(dto.endTime()) &&
                                existing.getEndTime().isAfter(dto.startTime())
                );

        if (!isAvailable) {
            throw new IllegalStateException("Time slot is already booked for this place.");
        }

        // Fake data fetch (replace with DB later)
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

        BookingMatch match = BookingMatch.builder()
                .id(UUID.randomUUID()) // simulate ID
                .place(place)
                .user(user)
                .team(team)
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .status(MatchStatus.PENDING)
                .build();

        dummyMatches.add(match);

        // Uncomment later when ready for DB
        // BookingMatch match = new BookingMatch();
        // match.setPlace(placeRepository.findById(dto.placeId()).orElseThrow(() -> new EntityNotFoundException("Place not found")));
        // match.setUser(userRepository.findById(dto.userId()).orElseThrow(() -> new EntityNotFoundException("User not found")));
        // match.setTeam(teamRepository.findById(dto.teamId()).orElseThrow(() -> new EntityNotFoundException("Team not found")));
        // match.setStartTime(dto.startTime());
        // match.setEndTime(dto.endTime());
        // match.setStatus(MatchStatus.PENDING);
        // return bookingMatchRepository.save(match);

        return match;
    }

    public BookingMatch cancelBooking(UUID matchId) {
        BookingMatch match = getById(matchId);
        match.setStatus(MatchStatus.CANCELLED);
        return match;
    }

    public BookingMatch getById(UUID id) {
        return dummyMatches.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));
        // return bookingMatchRepository.findById(id)
        //         .orElseThrow(() -> new EntityNotFoundException("Match not found"));
    }

    public List<BookingMatch> getByUser(UUID userId) {
        return dummyMatches.stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .toList();
        // return bookingMatchRepository.findByUserId(userId);
    }

    public List<BookingMatch> getByTeam(UUID teamId) {
        return dummyMatches.stream()
                .filter(m -> m.getTeam().getId().equals(teamId))
                .toList();
        // return bookingMatchRepository.findByTeamId(teamId);
    }

    public List<BookingMatch> getByPlace(UUID placeId) {
        return dummyMatches.stream()
                .filter(m -> m.getPlace().getId().equals(placeId))
                .toList();
        // return bookingMatchRepository.findByPlaceId(placeId);
    }

    public List<BookingMatch> getAll() {
        return new ArrayList<>(dummyMatches);
        // return bookingMatchRepository.findAll();
    }

    public void seedData(List<User> users, List<Place> places, List<Team> teams) {
        this.dummyUsers = users;
        this.dummyPlaces = places;
        this.dummyTeams = teams;
    }
}
