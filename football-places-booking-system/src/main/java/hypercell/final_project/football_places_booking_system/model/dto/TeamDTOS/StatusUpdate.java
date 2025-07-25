package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import hypercell.final_project.football_places_booking_system.model.enums.TeamStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdate(
        @NotNull TeamStatus status
) {
}
