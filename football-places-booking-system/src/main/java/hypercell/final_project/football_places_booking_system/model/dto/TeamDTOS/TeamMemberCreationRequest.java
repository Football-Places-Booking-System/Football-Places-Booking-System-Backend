package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;

public record TeamMemberCreationRequest(
        Long userId,
        Long teamId,
        TeamRole role,
        Long invitedById
) {
}
