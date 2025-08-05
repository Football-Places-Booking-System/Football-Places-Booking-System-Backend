package hypercell.final_project.football_places_booking_system.model.db;

import java.time.LocalDateTime;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.RequestType;
import hypercell.final_project.football_places_booking_system.model.enums.ResponseStatus;
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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = true, nullable = false)
    private UUID id;

    @Column(nullable = false, name = "send_time")
    private LocalDateTime sendTime;

    @Column(nullable = true, name = "response_time")
    private LocalDateTime responseTime;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    private ResponseStatus status;

    @Column(nullable = true, name = "requestMessage")
    private String requestMessage;

    @Column(nullable = true, name = "responseMessage")
    private String responseMessage;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // used to stoe the id of a TeamMember or MatchParticipant entity related to this request
    @Column(nullable = false, name = "joker_id")
    private UUID jokerId;
}