package hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs;

import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record MatchPartResponseDTO(
        UUID id,
        ParticipantStatus status,
        UUID bookingMatchId,
        UUID userId,
        String userEmail,
        String username
) {}