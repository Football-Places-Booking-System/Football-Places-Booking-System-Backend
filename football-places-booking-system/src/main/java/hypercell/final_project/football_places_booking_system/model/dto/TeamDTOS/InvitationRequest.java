package hypercell.final_project.football_places_booking_system.model.dto.TeamDTOS;

import jakarta.validation.constraints.Email;

public record InvitationRequest(
        @Email String email
) {}
