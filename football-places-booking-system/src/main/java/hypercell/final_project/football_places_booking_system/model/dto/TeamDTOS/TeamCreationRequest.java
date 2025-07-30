package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

public record TeamCreationRequest(
        String name,
        String description
) { }
//when creating a team by organizer he shall only enter those 2 fields then invite members but
//initially empty team
