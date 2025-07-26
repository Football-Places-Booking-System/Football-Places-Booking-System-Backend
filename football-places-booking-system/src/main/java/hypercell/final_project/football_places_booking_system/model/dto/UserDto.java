package hypercell.final_project.football_places_booking_system.model.dto;

import hypercell.final_project.football_places_booking_system.model.enums.UserRole;
import hypercell.final_project.football_places_booking_system.model.enums.UserStatus;

public record UserDTO(Long id, String username, String email, String password,
UserRole role, UserStatus status) {}
