package hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;

// DTO for transferring match participant data (user, match, status) between client and server.
public record MatchPartDTO(
        String email,
        UUID bookingMatchId,
        ParticipantStatus status
) {
}
