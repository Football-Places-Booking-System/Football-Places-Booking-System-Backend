package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

public record TeamMemberUpdateRequest(
        UUID id,
        TeamRole role,
        TeamStatus status
) { }
