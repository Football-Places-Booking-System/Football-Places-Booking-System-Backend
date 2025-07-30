package hypercell.final_project.football_places_booking_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.*;
import hypercell.final_project.football_places_booking_system.model.enums.ErrorCode;
import hypercell.final_project.football_places_booking_system.repository.BookingMatchRepository;
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
        User user = userRepository.findByEmailIgnoreCase(dto.email())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        // Use service instead of direct repository
        BookingMatch match = bookingMatchService.getById(dto.bookingMatchId());

        MatchParticipant participant = MatchParticipant.builder()
                .bookingMatch(match)
                .user(user)
                .status(ParticipantStatus.INVITED)
                .build();

        return matchParticipantRepository.save(participant);
    }

    // Respond to invitation
    public MatchParticipant respondToInvitation(UUID participantId, ParticipantStatus status) throws AppException {
        MatchParticipant p = matchParticipantRepository.findById(participantId)
                .orElseThrow(MatchParticipantNotFoundException::new);

        if (status == null) {
            throw new InvalidParticipantStatusException();
        }

        p.setStatus(status);
        p.setRespondedAt(LocalDateTime.now());

        return matchParticipantRepository.save(p);
    }

    // Get all participants for a match
    public List<MatchParticipant> getByMatch(UUID matchId) throws AppException {
        // Validate existence using service
        bookingMatchService.getById(matchId);
        return matchParticipantRepository.findByBookingMatchId(matchId);
    }
}
