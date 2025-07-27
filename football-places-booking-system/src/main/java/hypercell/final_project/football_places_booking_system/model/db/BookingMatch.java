package hypercell.final_project.football_places_booking_system.model.db;

import java.time.LocalDateTime;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a football match booking.
 * Links to Place, User, Team, and has a list of participants.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingMatch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Start and end times for the match booking.
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Status of the match (e.g., PENDING, CANCELLED).
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    // The place where the match is booked.
    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    // The user who made the booking.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // The team associated with the booking.
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    // List of participants in the match.
//    @OneToMany(mappedBy = "bookingMatch", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<MatchParticipant> participants = new ArrayList<>();
}
