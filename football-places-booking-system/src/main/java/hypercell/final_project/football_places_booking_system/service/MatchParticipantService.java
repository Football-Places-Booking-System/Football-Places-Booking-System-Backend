package hypercell.final_project.football_places_booking_system.service;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.MatchParticipantDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Service for managing match participants (invite, respond, list by match).
@Service
@RequiredArgsConstructor
public class MatchParticipantService {

    // In-memory list for demo/testing. Replace the repository for production.
    private final List<MatchParticipant> dummyParticipants = new ArrayList<>();
//    private final MatchParticipantRepository matchParticipantRepository;

    // Invites a user to a match and adds them as a participant.
    public MatchParticipant inviteParticipant(MatchParticipantDTO dto) {
        MatchParticipant participant = MatchParticipant.builder()
                .id((long) (dummyParticipants.size() + 1))
                .bookingMatch(BookingMatch.builder().id(dto.bookingMatchId()).build())
                .user(User.builder().id(dto.userId()).build())
                .status(ParticipantStatus.INVITED)
                .build();

        dummyParticipants.add(participant);
        // return matchParticipantRepository.save(participant); // Use later
        return participant;
    }

    // Updates the participant's status and response time.
    public MatchParticipant respondToInvitation(Long participantId, ParticipantStatus status) {
        MatchParticipant p = dummyParticipants.stream()
                .filter(mp -> mp.getId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        p.setStatus(status);
        p.setRespondedAt(LocalDateTime.now());

        // return matchParticipantRepository.save(p); // For DB
        return p;
    }

    // Returns all participants for a given match.
    public List<MatchParticipant> getByMatch(Long matchId) {
        return dummyParticipants.stream()
                .filter(p -> p.getBookingMatch().getId().equals(matchId))
                .toList();
        // return matchParticipantRepository.findByBookingMatchId(matchId);
    }
}
