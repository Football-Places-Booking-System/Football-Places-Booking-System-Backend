package hypercell.final_project.football_places_booking_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.*;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.repository.MatchParticipantRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchParticipantService {

    private final MatchParticipantRepository matchParticipantRepository;
    private final UserRepository userRepository;
    private final BookingMatchService bookingMatchService;

    // Invite a user to a match
    public MatchParticipant inviteParticipant(MatchPartDTO dto) throws AppException {
        // Validate email
        if (dto.email() == null || dto.email().trim().isEmpty()) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_EMAIL);
        }

        // Validate booking match ID
        if (dto.bookingMatchId() == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }

        User user = userRepository.findByEmailIgnoreCase(dto.email())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Use service instead of direct repository (this will validate booking match exists)
        BookingMatch match = bookingMatchService.getById(dto.bookingMatchId());

        // Check if user is already a participant in this match
        boolean alreadyParticipant = matchParticipantRepository
                .findByBookingMatchIdAndUserId(match.getId(), user.getId())
                .isPresent();

        if (alreadyParticipant) {
            throw new ValidationException(ErrorCode.MATCH_PARTICIPANT_ALREADY_EXISTS);
        }

        MatchParticipant participant = MatchParticipant.builder()
                .bookingMatch(match)
                .user(user)
                .status(ParticipantStatus.INVITED)
                .build();

        return matchParticipantRepository.save(participant);
    }

    // Respond to invitation
    public MatchParticipant respondToInvitation(UUID participantId, ParticipantStatus status) throws AppException {
        // Validate participant ID
        if (participantId == null) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_ID);
        }

        // Validate status
        if (status == null) {
            throw new ValidationException(ErrorCode.INVALID_PARTICIPANT_STATUS);
        }

        MatchParticipant participant = matchParticipantRepository.findById(participantId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MATCH_PARTICIPANT_NOT_FOUND));

        participant.setStatus(status);
        participant.setRespondedAt(LocalDateTime.now());

        return matchParticipantRepository.save(participant);
    }

    // Get all participants for a match
    public List<MatchParticipant> getByMatch(UUID matchId) throws AppException {
        // Validate match ID
        if (matchId == null) {
            throw new ValidationException(ErrorCode.INVALID_BOOKING_MATCH_ID);
        }

        // Validate existence using service (this will throw proper exception if not found)
        bookingMatchService.getById(matchId);
        
        return matchParticipantRepository.findByBookingMatchId(matchId);
    }
}
