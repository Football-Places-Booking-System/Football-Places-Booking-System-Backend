package hypercell.final_project.football_places_booking_system.model.dto.MatchPartDTOs;

import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
import hypercell.final_project.football_places_booking_system.model.enums.ParticipantStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserMatchResponseDTO(
        UUID matchId,
        UUID participantId,
        UUID teamId,
        String teamName,
        UUID placeId,
        String placeName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        MatchStatus bookingStatus,
        ParticipantStatus invitationStatus
) {}
