package hypercell.final_project.football_places_booking_system.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.UserRole;
import hypercell.final_project.football_places_booking_system.model.enums.UserStatus;

public record UserDTO(UUID id, String username, String email, String password,
                      UserRole role, UserStatus status, LocalDateTime createdAt) {}
