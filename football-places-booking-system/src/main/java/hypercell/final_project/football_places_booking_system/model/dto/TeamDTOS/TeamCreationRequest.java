package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import jakarta.validation.constraints.NotBlank;

public record TeamCreationRequest(
        @NotBlank String name,
        String description
) { }
//when creating a team by organizer he shall only enter those 2 fields then invite members but
//initially empty team
