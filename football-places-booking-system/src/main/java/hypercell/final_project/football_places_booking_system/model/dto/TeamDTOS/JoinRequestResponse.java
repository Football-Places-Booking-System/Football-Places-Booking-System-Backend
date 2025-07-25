package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

import java.time.LocalDateTime;

public record JoinRequestResponse(
        Long id,
        Long userId,
        String userName,
        LocalDateTime createdAt,
        TeamStatus status
) {
}
