package hypercell.final_project.football_places_booking_system.model.db;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingMatch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "bookingMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchParticipant> participants = new ArrayList<>();
}

