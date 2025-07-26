package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.enums.TeamRole;
import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;

public record TeamMemberUpdateRequest(
        Long id,
        TeamRole role,
        TeamStatus status
) { }
