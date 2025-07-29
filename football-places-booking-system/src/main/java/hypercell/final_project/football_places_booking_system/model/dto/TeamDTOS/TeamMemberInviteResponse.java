package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

import java.util.UUID;

public record TeamMemberInviteResponse(
        UUID teamMemberId,
        TeamRole role,
        TeamStatus status,
        UUID userId,
        UUID teamId,
        String userName,
        String teamName
) {
}
