package hypercell.final_project.football_places_booking_system.model.db;

import java.time.LocalDateTime;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity representing a participant in a football match booking.
// Links a user to a booking match and tracks their invitation status and response time.
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Status of the participant (e.g., INVITED, ACCEPTED, DECLINED).
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    // Timestamp when the participant responded to the invitation.
    private LocalDateTime respondedAt;

    // The match this participant is associated with.
    @ManyToOne
    @JoinColumn(name = "booking_match_id")
    private BookingMatch bookingMatch;

    // The user who is participating in the match.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
