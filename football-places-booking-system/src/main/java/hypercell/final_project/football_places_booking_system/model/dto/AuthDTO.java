package hypercell.final_project.football_places_booking_system.model.dto;

import hypercell.final_project.football_places_booking_system.model.enums.UserRole;

public record AuthDTO (java.util.UUID id, String token, UserRole role) {
}
