package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

import java.util.UUID;

public record TeamMemberInvitationDTO(
        TeamStatus status
) {
}
