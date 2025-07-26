package hypercell.final_project.football_places_booking_system.model.dto;

import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;


// DTO for transferring match participant data (user, match, status) between client and server.
public record MatchParticipantDTO(
        String email,
        Long bookingMatchId,
        ParticipantStatus status
) {
}
