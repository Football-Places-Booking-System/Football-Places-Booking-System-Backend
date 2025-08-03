package hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs;

import hypercell.final_project.football_places_booking_system.model.enums.MatchStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BookingDetailRespDTO(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        MatchStatus status,
        LocalDateTime createdAt,

        UUID placeId,
        String placeName,

        UUID teamId,
        String teamName,

        UUID userId,
        String userName
) {}
