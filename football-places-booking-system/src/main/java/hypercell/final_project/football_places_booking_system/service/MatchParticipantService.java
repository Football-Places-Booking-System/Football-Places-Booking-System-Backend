package hypercell.final_project.football_places_booking_system.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.MatchParticipant;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.MatchParticipantDTO;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchParticipantService {

    // In-memory lists for demo/testing.
    private final List<MatchParticipant> dummyParticipants = new ArrayList<>();
    private final List<User> dummyUsers = new ArrayList<>();

    public void seedUsers(List<User> users) {
        this.dummyUsers.clear();
        this.dummyUsers.addAll(users);
    }

    // Repositories for future production use.
    // Private final MatchParticipantRepository matchParticipantRepository;
    // private final UserRepository userRepository;

    // Invites a user to a match and adds them as a participant.
    public MatchParticipant inviteParticipant(MatchParticipantDTO dto) {
        // Dummy version
        User user = dummyUsers.stream()
                .filter(u -> u.getEmail().equals(dto.email()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User with email not found"));

        MatchParticipant participant = MatchParticipant.builder()
                .id((long) (dummyParticipants.size() + 1)) // simulate ID
                .bookingMatch(BookingMatch.builder().id(dto.bookingMatchId()).build())
                .user(user)
                .status(ParticipantStatus.INVITED)
                .build();

        dummyParticipants.add(participant);
        return participant;

        // Real version (uncomment when ready)
        /*
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("User with email not found"));

        BookingMatch match = bookingMatchRepository.findById(dto.bookingMatchId())
                .orElseThrow(() -> new EntityNotFoundException("Booking match not found"));

        MatchParticipant participant = MatchParticipant.builder()
                .bookingMatch(match)
                .user(user)
                .status(ParticipantStatus.INVITED)
                .build();

        return matchParticipantRepository.save(participant);
        */
    }

    // Updates the participant's status and response time.
    public MatchParticipant respondToInvitation(Long participantId, ParticipantStatus status) {
        // Dummy version
        MatchParticipant p = dummyParticipants.stream()
                .filter(mp -> mp.getId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        p.setStatus(status);
        p.setRespondedAt(LocalDateTime.now());
        return p;

        // Real version (uncomment when ready)
        /*
        MatchParticipant p = matchParticipantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        p.setStatus(status);
        p.setRespondedAt(LocalDateTime.now());

        return matchParticipantRepository.save(p);
        */
    }

    // Returns all participants for a given match.
    public List<MatchParticipant> getByMatch(Long matchId) {
        // Dummy version
        return dummyParticipants.stream()
                .filter(p -> p.getBookingMatch().getId().equals(matchId))
                .toList();

        // Real version (uncomment when ready)
        // return matchParticipantRepository.findByBookingMatchId(matchId);
    }
}
