package hypercell.final_project.football_places_booking_system.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.*;
import hypercell.final_project.football_places_booking_system.model.db.Request;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDetailRespDTO;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.UserMatchResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS.InvitationRequest;
import hypercell.final_project.football_places_booking_system.model.enums.*;
import hypercell.final_project.football_places_booking_system.service.Interfaces.*;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.repository.MatchParticipantRepository;
import hypercell.final_project.football_places_booking_system.repository.RequestRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchParticipantServiceImpl implements MatchParticipantService {

    private final MatchParticipantRepository matchParticipantRepository;
    private final UserRepository userRepository;
    private final TeamMemberService teamMemberService;
    private final BookingMatchServiceImpl bookingMatchService;
    private final EmailService emailService;
    private final RequestService requestService;
    private final RequestRepository requestRepository;

    // ==============================
    // Shared Validation Utilities
    // ==============================

    private BookingMatch validateBookingMatch(UUID bookingMatchId) throws AppException {
        if (bookingMatchId == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }
        return bookingMatchService.getById(bookingMatchId);
    }

    private User validateUserByEmail(String email) throws AppException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_EMAIL);
        }
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateCapacity(BookingMatch match) throws AppException {
        int maxCapacity = switch (match.getPlace().getPlaceType()) {
            case FIVE -> 10;
            case SEVEN -> 14;
            case ELEVEN -> 22;
        };

        long acceptedCount = matchParticipantRepository.findByBookingMatchId(match.getId())
                .stream()
                .filter(p -> p.getStatus() == ParticipantStatus.ACCEPTED)
                .count();

        if (acceptedCount >= maxCapacity) {
            throw new ValidationException(ErrorCode.MATCH_CAPACITY_EXCEEDED);
        }
    }

    private void updateMatchStatusIfFull(BookingMatch match) throws AppException {
        int maxCapacity = switch (match.getPlace().getPlaceType()) {
            case FIVE -> 10;
            case SEVEN -> 14;
            case ELEVEN -> 22;
        };

        long acceptedCount = matchParticipantRepository.findByBookingMatchId(match.getId())
                .stream()
                .filter(p -> p.getStatus() == ParticipantStatus.ACCEPTED)
                .count();

        if (acceptedCount >= maxCapacity && match.getStatus() == MatchStatus.PENDING_PLAYERS) {
            match.setStatus(MatchStatus.PENDING_PAYMENT);
            bookingMatchService.getById(match.getId()); // ensure match exists
            bookingMatchService.save(match); // you might need a save method or repo
        }
    }


    private void ensureNotParticipant(UUID matchId, UUID userId) throws AppException {
        boolean alreadyParticipant = matchParticipantRepository
                .findByBookingMatchIdAndUserId(matchId, userId)
                .isPresent();

        if (alreadyParticipant) {
            throw new ValidationException(ErrorCode.MATCH_PARTICIPANT_ALREADY_EXISTS);
        }
    }

    private String buildInvitationMessage(BookingMatch match, String senderName) {
        String matchDate = match.getStartTime().toLocalDate().toString();

        String startTime = formatHour(match.getStartTime().getHour());
        String endTime = formatHour(match.getEndTime().getHour());

        return String.format("%s has invited you to join match at %s with team %s at %s from %s to %s",
                senderName, match.getPlace().getName(), match.getTeam().getName(), matchDate, startTime, endTime);
    }

    private String formatHour(int hour) {
        String amPm = hour >= 12 ? "pm" : "am";
        int displayHour = (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour);
        return String.format("%d %s", displayHour, amPm);
    }

    // ==============================
    // Invite a User
    // ==============================

    @Override
    public MatchParticipant inviteParticipant(InvitationRequest dto, UUID bookingMatchId) throws AppException {
        User user = validateUserByEmail(dto.email());
        BookingMatch match = validateBookingMatch(bookingMatchId);

        ensureNotParticipant(match.getId(), user.getId());

        MatchParticipant participant = matchParticipantRepository.save(
                MatchParticipant.builder()
                        .bookingMatch(match)
                        .user(user)
                        .status(ParticipantStatus.INVITED)
                        .build()
        );

        String message = buildInvitationMessage(match, match.getUser().getUserName());
        requestService.createRequestWithMessage(match.getUser().getId(), user.getId(),
                RequestType.MATCH_INVITATION, message, participant.getId());

        emailService.sendInvitationEmail(dto.email(), participant);
        return participant;
    }

    // ==============================
    // Respond to Invitation
    // ==============================

    @Override
    public MatchParticipant respondToInvitation(UUID participantId, ParticipantStatus status) throws AppException {
        if (participantId == null) throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_ID);
        if (status == null) throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_STATUS);

        MatchParticipant participant = matchParticipantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MATCH_PARTICIPANT_NOT_FOUND));

        if (participant.getStatus() != ParticipantStatus.INVITED) {
            throw new AlreadyExistsException(ErrorCode.MATCH_PARTICIPANT_ALREADY_RESPONDED);
        }

        if (status == ParticipantStatus.ACCEPTED) {
            validateCapacity(participant.getBookingMatch());
        }

        participant.setStatus(status);
        participant.setRespondedAt(LocalDateTime.now());

        // Update request notification
        Request existingRequest = requestRepository.findByJokerId(participantId);
        String responseMsg = String.format("%s has %s the match invitation",
                participant.getUser().getUserName(),
                (status == ParticipantStatus.ACCEPTED ? "accepted" : "declined"));

        requestService.updateRequestStatusWithMessage(existingRequest.getId(),
                status == ParticipantStatus.ACCEPTED ? ResponseStatus.ACCEPTED : ResponseStatus.REJECTED,
                responseMsg);

        // ✅ Save participant
        MatchParticipant savedParticipant = matchParticipantRepository.save(participant);

        // ✅ Check and update match status
        updateMatchStatusIfFull(savedParticipant.getBookingMatch());

        emailService.sendResponseToMatchParticipantInvitation(participant, status);
        return savedParticipant;
    }

    public BookingMatch save(BookingMatch match) {
        return bookingMatchService.save(match);
    }

    // ==============================
    // Join Match as Organizer
    // ==============================

    @Override
    public MatchParticipant joinMatchAsOrganizer(UUID bookingMatchId, UUID organizerId) throws AppException {
        BookingMatch match = validateBookingMatch(bookingMatchId);

        // ✅ Allow any organizer of the team to join, not just match creator
        if (!teamMemberService.isOrganizer(organizerId, match.getTeam().getId())) {
            throw new ForbiddenActionException(ErrorCode.FORBIDDEN);
        }

        ensureNotParticipant(match.getId(), organizerId);
        validateCapacity(match);

        var organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return matchParticipantRepository.save(
                MatchParticipant.builder()
                        .bookingMatch(match)
                        .user(organizer) // ✅ Use the actual joining organizer, not match.getUser()
                        .status(ParticipantStatus.ACCEPTED)
                        .respondedAt(LocalDateTime.now())
                        .build()
        );
    }


    // Get all participants for a match
    public List<MatchParticipant> getByMatch(UUID matchId) throws AppException {
        // Validate match ID
        if (matchId == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }

        // Validate existence using service (this will throw a proper exception if not found)
        bookingMatchService.getById(matchId);

        return matchParticipantRepository.findByBookingMatchId(matchId);
    }

    // Get all matches that a user has participated in
    public List<UserMatchResponseDTO> getUserParticipatedMatches(UUID userId) throws AppException {
        if (userId == null) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_ID);
        }

        // Validate user existence
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Map to booking matches
        return matchParticipantRepository.findByUserId(userId).stream()
                .map(mp -> UserMatchResponseDTO.builder()
                        .matchId(mp.getBookingMatch().getId())
                        .participantId(mp.getId())   // Participant primary key
                        .teamId(mp.getBookingMatch().getTeam().getId())
                        .teamName(mp.getBookingMatch().getTeam().getName())
                        .placeId(mp.getBookingMatch().getPlace().getId())
                        .placeName(mp.getBookingMatch().getPlace().getName())
                        .startTime(mp.getBookingMatch().getStartTime())
                        .endTime(mp.getBookingMatch().getEndTime())
                        .bookingStatus(mp.getBookingMatch().getStatus())
                        .invitationStatus(mp.getStatus())   // INVITED, ACCEPTED, DECLINED
                        .build())
                .toList();
    }

    public List<BookingDetailRespDTO> getUserParticipatedMatchesDetailed(UUID userId) throws AppException {
        if (userId == null) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_ID);
        }

        // Validate user
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Fetch all participant entries for this user
        List<MatchParticipant> participatedMatches = matchParticipantRepository.findByUserId(userId);

        // Map to detailed response DTO
        return participatedMatches.stream()
                .map(mp -> {
                    BookingMatch match = mp.getBookingMatch();
                    return BookingDetailRespDTO.builder()
                            .id(match.getId())
                            .startTime(match.getStartTime())
                            .endTime(match.getEndTime())
                            .status(match.getStatus())
                            .createdAt(match.getCreatedAt())

                            .placeId(match.getPlace().getId())
                            .placeName(match.getPlace().getName())

                            .teamId(match.getTeam().getId())
                            .teamName(match.getTeam().getName())

                            .userId(match.getUser().getId())
                            .userName(match.getUser().getUserName())
                            .build();
                })
                .distinct()
                .toList();
    }

}
