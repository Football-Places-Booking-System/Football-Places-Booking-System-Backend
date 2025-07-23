package hypercell.final_project.football_places_booking_system.model.db;

import java.time.LocalDateTime;

import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    private LocalDateTime respondedAt;

    @ManyToOne
    @JoinColumn(name = "booking_match_id")
    private BookingMatch bookingMatch;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

