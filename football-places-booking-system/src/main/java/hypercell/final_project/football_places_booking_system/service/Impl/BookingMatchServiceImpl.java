package hypercell.final_project.football_places_booking_system.service.Impl;

import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.exception.ForbiddenActionException;
import hypercell.final_project.football_places_booking_system.exception.NotFoundException;
import hypercell.final_project.football_places_booking_system.exception.ValidationException;
import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.repository.BookingMatchRepository;
import hypercell.final_project.football_places_booking_system.repository.PlaceRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamMemberRepository;
import hypercell.final_project.football_places_booking_system.repository.TeamRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import hypercell.final_project.football_places_booking_system.service.Interfaces.TeamMemberService;
import hypercell.final_project.football_places_booking_system.service.Interfaces.BookingMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingMatchServiceImpl implements BookingMatchService {

    private final BookingMatchRepository bookingMatchRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberService teamMemberService;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * Create a new match booking. Only ORGANIZER can book a match.
     */
    public BookingMatch createBookingMatch(BookingDTO dto, UUID userId) throws AppException {

        if (dto.teamId() == null) {
            throw new ValidationException(ErrorCode.INVALID_TEAM_ID);
        }
        if (dto.placeId() == null) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_ID);
        }
        if (dto.startTime() == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_START_TIME);
        }
        if (dto.endTime() == null || dto.startTime().isAfter(dto.endTime())) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_END_TIME);
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        if (!teamMemberService.isOrganizer(user.getId(), dto.teamId())) {
            throw new ForbiddenActionException(ErrorCode.FORBIDDEN);
        }

        var place = placeRepository.findById(dto.placeId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));

        var team = teamRepository.findById(dto.teamId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        boolean isAvailable = bookingMatchRepository.findByPlaceId(place.getId()).stream()
                .noneMatch(existing ->
                        existing.getStartTime().isBefore(dto.endTime()) &&
                                existing.getEndTime().isAfter(dto.startTime())
                );

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
        if (matchId == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }

        BookingMatch match = getById(matchId);

        if (!teamMemberService.isOrganizer(userId, match.getTeam().getId())) {
            throw new ForbiddenActionException(ErrorCode.FORBIDDEN);
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

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return bookingMatchRepository.findByUserId(userId);
    }

    public List<BookingMatch> getByTeam(UUID teamId) throws AppException {
        if (teamId == null) {
            throw new ValidationException(ErrorCode.INVALID_TEAM_ID);
        }

        teamRepository.findById(teamId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));

        return bookingMatchRepository.findByTeamId(teamId);
    }

    public List<BookingMatch> getByPlace(UUID placeId) throws AppException {
        if (placeId == null) {
            throw new ValidationException(ErrorCode.INVALID_PLACE_ID);
        }

        placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLACE_NOT_FOUND));

        return bookingMatchRepository.findByPlaceId(placeId);
    }

    public List<BookingMatch> getAll() {
        return bookingMatchRepository.findAll();
    }

    /**
     * Get matches for all teams where this user is an ORGANIZER.
     */
    public List<BookingMatch> getMyMatchesAsOrganizer(UUID userId) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        List<UUID> organizerTeams = teamMemberRepository.findByUserId(userId).stream()
                .filter(tm -> tm.getRole() == TeamRole.ORGANIZER && tm.getStatus() == TeamStatus.APPROVED)
                .map(tm -> tm.getTeam().getId())
                .toList();

        if (organizerTeams.isEmpty()) {
            return List.of();
        }

        return bookingMatchRepository.findAll().stream()
                .filter(match -> organizerTeams.contains(match.getTeam().getId()))
                .toList();
    }

    public List<BookingMatch> getMyMatchesAsPlayer(UUID userId) throws AppException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        List<UUID> playerTeams = teamMemberRepository.findByUserId(userId).stream()
                .filter(tm -> tm.getRole() == TeamRole.PLAYER && tm.getStatus() == TeamStatus.APPROVED)
                .map(tm -> tm.getTeam().getId())
                .toList();

        if (playerTeams.isEmpty()) {
            return List.of();
        }

        return bookingMatchRepository.findAll().stream()
                .filter(match -> playerTeams.contains(match.getTeam().getId()))
                .toList();
    }

}
