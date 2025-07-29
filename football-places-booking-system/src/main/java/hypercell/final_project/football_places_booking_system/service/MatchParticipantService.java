package hypercell.final_project.football_places_booking_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.repository.BookingMatchRepository;
import hypercell.final_project.football_places_booking_system.repository.MatchParticipantRepository;
import hypercell.final_project.football_places_booking_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs.MatchPartDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchParticipantService {

    // Repositories for future production use.
     private final MatchParticipantRepository matchParticipantRepository;
     private final UserRepository userRepository;
     private final BookingMatchRepository bookingMatchRepository;

    // Invites a user to a match and adds them as a participant.
    public MatchParticipant inviteParticipant(MatchPartDTO dto) {
        User user = userRepository.findByEmailIgnoreCase(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("User with email not found"));

        BookingMatch match = bookingMatchRepository.findById(dto.bookingMatchId())
                .orElseThrow(() -> new EntityNotFoundException("Booking match not found"));

        MatchParticipant participant = MatchParticipant.builder()
                .bookingMatch(match)
                .user(user)
                .status(ParticipantStatus.INVITED)
                .build();

        return matchParticipantRepository.save(participant);

    }

    // Updates the participant's status and response time.
    public MatchParticipant respondToInvitation(UUID participantId, ParticipantStatus status) {
        MatchParticipant p = matchParticipantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        p.setStatus(status);
        p.setRespondedAt(LocalDateTime.now());

        return matchParticipantRepository.save(p);
    }

    // Returns all participants for a given match.
    public List<MatchParticipant> getByMatch(UUID matchId) {
         return matchParticipantRepository.findByBookingMatchId(matchId);
    }
}
