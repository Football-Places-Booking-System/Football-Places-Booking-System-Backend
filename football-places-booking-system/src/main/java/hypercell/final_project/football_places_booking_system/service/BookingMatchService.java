package hypercell.final_project.football_places_booking_system.service;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDTO;
import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
import hypercell.final_project.football_places_booking_system.repository.*;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
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
    private final TeamMemberService teamMemberService;

    public BookingMatch createBookingMatch(BookingDTO dto) {
        try {
            if (!teamMemberService.isOrganizer(dto.userId(), dto.teamId())) {
                throw new SecurityException("Only team organizers can create bookings.");
            }
        } catch (NotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }

        var place = placeRepository.findById(dto.placeId())
                .orElseThrow(() -> new EntityNotFoundException("Place not found"));
        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        var team = teamRepository.findById(dto.teamId())
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        boolean isAvailable = bookingMatchRepository.findByPlaceId(place.getId()).stream()
                .noneMatch(existing ->
                        existing.getStartTime().isBefore(dto.endTime()) &&
                                existing.getEndTime().isAfter(dto.startTime()));

        if (!isAvailable) {
            throw new IllegalStateException("The selected time slot is already booked for this place.");
        }

        BookingMatch match = new BookingMatch();
        match.setPlace(place);
        match.setUser(user);
        match.setTeam(team);
        match.setStartTime(dto.startTime());
        match.setEndTime(dto.endTime());
        match.setStatus(MatchStatus.PENDING);

        return bookingMatchRepository.save(match);
    }

    public BookingMatch cancelBooking(UUID matchId, UUID userId) {
        BookingMatch match = getById(matchId);
        try {
            if (!teamMemberService.isOrganizer(userId, match.getTeam().getId())) {
                throw new SecurityException("Only team organizers can cancel bookings.");
            }
        } catch (NotFoundException e) {
            throw new EntityNotFoundException(e.getMessage());
        }

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
