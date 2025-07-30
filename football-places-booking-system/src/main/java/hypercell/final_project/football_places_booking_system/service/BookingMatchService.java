package hypercell.final_project.football_places_booking_system.service;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.*;
import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
import hypercell.final_project.football_places_booking_system.repository.*;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
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

    public BookingMatch createBookingMatch(BookingDTO dto) throws AppException {
        // Validate user ID
        if (dto.userId() == null) {
            throw new ValidationException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        // Validate team ID
        if (dto.teamId() == null) {
            throw new ValidationException(ErrorCode.INVALID_TEAM_ID);
        }

        // Validate place ID
        if (dto.placeId() == null) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_ID);
        }

        // Validate start time
        if (dto.startTime() == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_START_TIME);
        }

        // Validate end time
        if (dto.endTime() == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_END_TIME);
        }

        // Validate time order
        if (dto.startTime().isAfter(dto.endTime()) || dto.startTime().isEqual(dto.endTime())) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_END_TIME);
        }

        // Check if user is organizer
        try {
            if (!teamMemberService.isOrganizer(dto.userId(), dto.teamId())) {
                throw new ForbiddenActionException();
            }
        } catch (NotFoundException e) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        var place = placeRepository.findById(dto.placeId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));
        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        var team = teamRepository.findById(dto.teamId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        // Check time slot availability
        boolean isAvailable = bookingMatchRepository.findByPlaceId(place.getId()).stream()
                .noneMatch(existing ->
                        existing.getStartTime().isBefore(dto.endTime()) &&
                                existing.getEndTime().isAfter(dto.startTime()));

        if (!isAvailable) {
            throw new ValidationException(ErrorCode.TIME_SLOT_UNAVAILABLE);
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

    public void cancelBooking(UUID matchId, UUID userId) throws AppException {
        // Validate match ID
        if (matchId == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }

        // Validate user ID
        if (userId == null) {
            throw new ValidationException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        BookingMatch match = getById(matchId);
        
        try {
            if (!teamMemberService.isOrganizer(userId, match.getTeam().getId())) {
                throw new ForbiddenActionException();
            }
        } catch (NotFoundException e) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        match.setStatus(MatchStatus.CANCELLED);
        bookingMatchRepository.save(match);
    }

    public BookingMatch getById(UUID id) throws AppException {
        if (id == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }

        return bookingMatchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.BOOKING_MATCH_NOT_FOUND));
    }

    public List<BookingMatch> getByUser(UUID userId) throws AppException {
        if (userId == null) {
            throw new ValidationException(ErrorCode.INVALID_REQUEST_TYPE);
        }

        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return bookingMatchRepository.findByUserId(userId);
    }

    public List<BookingMatch> getByTeam(UUID teamId) throws AppException {
        if (teamId == null) {
            throw new ValidationException(ErrorCode.INVALID_TEAM_ID);
        }

        // Validate team exists
        teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        return bookingMatchRepository.findByTeamId(teamId);
    }

    public List<BookingMatch> getByPlace(UUID placeId) throws AppException {
        if (placeId == null) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_ID);
        }

        // Validate place exists
        placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));

        return bookingMatchRepository.findByPlaceId(placeId);
    }

    public List<BookingMatch> getAll() {
        return bookingMatchRepository.findAll();
    }
}
