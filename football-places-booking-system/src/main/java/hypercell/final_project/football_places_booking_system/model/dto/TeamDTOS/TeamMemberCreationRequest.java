package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;

public record TeamMemberCreationRequest(
        UUID userId,
        UUID teamId,
        TeamRole role,
        UUID invitedById
) {
}
